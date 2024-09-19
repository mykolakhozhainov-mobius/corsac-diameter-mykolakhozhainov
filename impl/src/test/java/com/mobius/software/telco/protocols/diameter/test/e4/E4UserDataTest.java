package com.mobius.software.telco.protocols.diameter.test.e4;
/*
 * Mobius Software LTD
 * Copyright 2019 - 2023, Mobius Software LTD and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.restcomm.cluster.ClusteredID;

import com.mobius.software.telco.protocols.diameter.ApplicationIDs;
import com.mobius.software.telco.protocols.diameter.AsyncCallback;
import com.mobius.software.telco.protocols.diameter.DiameterLink;
import com.mobius.software.telco.protocols.diameter.DiameterSession;
import com.mobius.software.telco.protocols.diameter.DiameterStack;
import com.mobius.software.telco.protocols.diameter.NetworkListener;
import com.mobius.software.telco.protocols.diameter.ResultCodes;
import com.mobius.software.telco.protocols.diameter.app.ClientAuthSessionStateless;
import com.mobius.software.telco.protocols.diameter.app.ServerAuthSessionStateless;
import com.mobius.software.telco.protocols.diameter.app.SessionStateEnum;
import com.mobius.software.telco.protocols.diameter.app.e4.ClientListener;
import com.mobius.software.telco.protocols.diameter.app.e4.ServerListener;
import com.mobius.software.telco.protocols.diameter.app.e4.E4ClientSession;
import com.mobius.software.telco.protocols.diameter.commands.DiameterMessage;
import com.mobius.software.telco.protocols.diameter.commands.DiameterRequest;
import com.mobius.software.telco.protocols.diameter.commands.e4.E4Request;
import com.mobius.software.telco.protocols.diameter.commands.e4.UserDataAnswer;
import com.mobius.software.telco.protocols.diameter.commands.e4.UserDataRequest;
import com.mobius.software.telco.protocols.diameter.commands.e4.E4Answer;
import com.mobius.software.telco.protocols.diameter.exceptions.DiameterException;
import com.mobius.software.telco.protocols.diameter.impl.app.e4.E4ProviderImpl;


/**
*
* @author yulian oifa
*
*/
public class E4UserDataTest extends NetworkTestBase
{
	protected static final String localListenerID = "1";
	private static final Logger logger = LogManager.getLogger(E4UserDataTest.class);
	
	@Test
	public void testUserData() throws Exception
	{
		super.setupRemote(0L,0L);
		super.setupLocal(0L,0L);
		
		final AtomicLong udaReceived=new AtomicLong(0L);
		final AtomicLong udaReceivedByListener=new AtomicLong(0L);
		final AtomicLong udrReceived=new AtomicLong(0L);
		final AtomicLong udrReceivedByListener=new AtomicLong(0L);
		final AtomicLong timeoutReceived=new AtomicLong(0L);
		
		DiameterStack localStack = this.localStack;
		DiameterStack serverStack = this.serverStack;
		
		localStack.getNetworkManager().addNetworkListener(localListenerID, new NetworkListener()
		{
			@Override
			public void onMessage(DiameterMessage message, String linkID, AsyncCallback callback)
			{
				if(message instanceof E4Answer)
					udrReceived.incrementAndGet();				
			}
		});
		
		serverStack.getNetworkManager().addNetworkListener(localListenerID, new NetworkListener()
		{
			@Override
			public void onMessage(DiameterMessage message, String linkID, AsyncCallback callback)
			{
				if(message instanceof E4Request)
					udaReceived.incrementAndGet();				
			}
		});
		
		try
		{
			Thread.sleep(reconnectTimeout * 2);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		E4ProviderImpl provider = (E4ProviderImpl)localStack.getProvider(Long.valueOf(ApplicationIDs.E4), Package.getPackage("com.mobius.software.telco.protocols.diameter.commands.e4"));
		ClusteredID<?> listenerID=generator.generateID();
		provider.setClientListener(listenerID, new ClientListener()
		{
			@Override
			public void onTimeout(DiameterRequest request,DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}
			
			@Override
			public void onIdleTimeout(DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}

			@Override
			public void onInitialAnswer(E4Answer answer, ClientAuthSessionStateless<E4Request> session, String linkID, AsyncCallback callback)
			{
				udaReceivedByListener.incrementAndGet();
			}
		});
		
		E4ProviderImpl serverProvider = (E4ProviderImpl)serverStack.getProvider(Long.valueOf(ApplicationIDs.E4), Package.getPackage("com.mobius.software.telco.protocols.diameter.commands.e4"));
		serverProvider.setServerListener(listenerID, new ServerListener()
		{
			@Override
			public void onTimeout(DiameterRequest request,DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}
			
			@Override
			public void onIdleTimeout(DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}

			@Override
			public void onInitialRequest(E4Request request, ServerAuthSessionStateless<E4Answer> session, String linkID, AsyncCallback callback)
			{
				udrReceivedByListener.incrementAndGet();
				UserDataRequest udr=(UserDataRequest)request;
				try
				{
					UserDataAnswer uda = serverProvider.getMessageFactory().createUserDataAnswer(udr, udr.getHopByHopIdentifier(), udr.getEndToEndIdentifier(), ResultCodes.DIAMETER_SUCCESS);
					session.sendInitialAnswer(uda, new AsyncCallback()
					{
						@Override
						public void onSuccess()
						{
							
						}
						
						@Override
						public void onError(DiameterException ex)
						{
							logger.error("An error occured while sending User Data Answer," + ex.getMessage(),ex);
						}
					});
				}
				catch(DiameterException ex)
				{
					logger.error("An error occured while sending User Data Answer," + ex.getMessage(),ex);
				}
			}
		});
		
		//usually its not needed to get link, here we use it to read hosts/realms
		DiameterLink localLink = localStack.getNetworkManager().getLink(localLinkID);
		
		UserDataRequest request = provider.getMessageFactory().createUserDataRequest(localLink.getLocalHost(), localLink.getLocalRealm(), localLink.getDestinationHost(), localLink.getDestinationRealm());
		E4ClientSession clientSession = (E4ClientSession)provider.getSessionFactory().createClientSession(request);
		clientSession.sendInitialRequest(request, new AsyncCallback()
		{
			@Override
			public void onSuccess()
			{
			}
			
			@Override
			public void onError(DiameterException ex)
			{
				logger.error("An error occured while sending User Data Request," + ex.getMessage(),ex);
			}
		});
		
		try
		{
			Thread.sleep(responseTimeout);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		assertEquals(clientSession.getSessionState(),SessionStateEnum.IDLE);
		
		//make sure no timeout is processed
		try
		{
			Thread.sleep(idleTimeout * 2);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		super.stopLocal();
		super.stopRemote();
		
		try
		{
			Thread.sleep(responseTimeout);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		assertEquals(udaReceived.get() , 1L);
		assertEquals(udaReceivedByListener.get() , 1L);
		assertEquals(udrReceived.get() , 1L);
		assertEquals(udrReceivedByListener.get() , 1L);
		assertEquals(timeoutReceived.get() , 0L);
	}
	
	@Test
	public void testDuplicateUserData() throws Exception
	{
		super.setupRemote(1000L,2000L);
		super.setupLocal(1000L,2000L);
		
		final AtomicLong udaReceived=new AtomicLong(0L);
		final AtomicLong udaReceivedByListener=new AtomicLong(0L);
		final AtomicLong udrReceived=new AtomicLong(0L);
		final AtomicLong udrReceivedByListener=new AtomicLong(0L);
		final AtomicLong timeoutReceived=new AtomicLong(0L);
		
		DiameterStack localStack = this.localStack;
		DiameterStack serverStack = this.serverStack;
		
		localStack.getNetworkManager().addNetworkListener(localListenerID, new NetworkListener()
		{
			@Override
			public void onMessage(DiameterMessage message, String linkID, AsyncCallback callback)
			{
				if(message instanceof E4Answer)
					udrReceived.incrementAndGet();				
			}
		});
		
		serverStack.getNetworkManager().addNetworkListener(localListenerID, new NetworkListener()
		{
			@Override
			public void onMessage(DiameterMessage message, String linkID, AsyncCallback callback)
			{
				if(message instanceof E4Request)
					udaReceived.incrementAndGet();				
			}
		});
		
		try
		{
			Thread.sleep(reconnectTimeout * 2);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		E4ProviderImpl provider = (E4ProviderImpl)localStack.getProvider(Long.valueOf(ApplicationIDs.E4), Package.getPackage("com.mobius.software.telco.protocols.diameter.commands.e4"));
		ClusteredID<?> listenerID=generator.generateID();
		provider.setClientListener(listenerID, new ClientListener()
		{
			@Override
			public void onTimeout(DiameterRequest request,DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}
			
			@Override
			public void onIdleTimeout(DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}

			@Override
			public void onInitialAnswer(E4Answer answer, ClientAuthSessionStateless<E4Request> session, String linkID, AsyncCallback callback)
			{
				udaReceivedByListener.incrementAndGet();
			}
		});
		
		E4ProviderImpl serverProvider = (E4ProviderImpl)serverStack.getProvider(Long.valueOf(ApplicationIDs.E4), Package.getPackage("com.mobius.software.telco.protocols.diameter.commands.e4"));
		serverProvider.setServerListener(listenerID, new ServerListener()
		{
			@Override
			public void onTimeout(DiameterRequest request,DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}
			
			@Override
			public void onIdleTimeout(DiameterSession session)
			{
				timeoutReceived.incrementAndGet();
			}

			@Override
			public void onInitialRequest(E4Request request, ServerAuthSessionStateless<E4Answer> session, String linkID, AsyncCallback callback)
			{
				udrReceivedByListener.incrementAndGet();
				UserDataRequest udr=(UserDataRequest)request;
				try
				{
					UserDataAnswer uda = serverProvider.getMessageFactory().createUserDataAnswer(udr, udr.getHopByHopIdentifier(), udr.getEndToEndIdentifier(), ResultCodes.DIAMETER_SUCCESS);
					session.sendInitialAnswer(uda, new AsyncCallback()
					{
						@Override
						public void onSuccess()
						{
							
						}
						
						@Override
						public void onError(DiameterException ex)
						{
							logger.error("An error occured while sending User Data Answer," + ex.getMessage(),ex);
						}
					});
				}
				catch(DiameterException ex)
				{
					logger.error("An error occured while sending User Data Answer," + ex.getMessage(),ex);
				}
			}
		});
		
		//usually its not needed to get link, here we use it to read hosts/realms
		DiameterLink localLink = localStack.getNetworkManager().getLink(localLinkID);
		
		UserDataRequest request = provider.getMessageFactory().createUserDataRequest(localLink.getLocalHost(), localLink.getLocalRealm(), localLink.getDestinationHost(), localLink.getDestinationRealm());
		E4ClientSession clientSession = (E4ClientSession)provider.getSessionFactory().createClientSession(request);
		clientSession.sendInitialRequest(request, new AsyncCallback()
		{
			@Override
			public void onSuccess()
			{
			}
			
			@Override
			public void onError(DiameterException ex)
			{
				logger.error("An error occured while sending User Data Request," + ex.getMessage(),ex);
			}
		});
		
		try
		{
			Thread.sleep(responseTimeout);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		assertEquals(clientSession.getSessionState(),SessionStateEnum.IDLE);
		
		localLink.sendMessage(request, new AsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				
			}
			
			@Override
			public void onError(DiameterException ex)
			{
				
			}
		});
		
		//make sure no timeout is processed
		try
		{
			Thread.sleep(idleTimeout * 2);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		super.stopLocal();
		super.stopRemote();
		
		try
		{
			Thread.sleep(responseTimeout);
		}
		catch(InterruptedException ex)
		{
			
		}
		
		assertEquals(udaReceived.get() , 1L);
		assertEquals(udaReceivedByListener.get() , 1L);
		assertEquals(udrReceived.get() , 2L);
		assertEquals(udrReceivedByListener.get() , 1L);
		assertEquals(timeoutReceived.get() , 0L);
	}		
}