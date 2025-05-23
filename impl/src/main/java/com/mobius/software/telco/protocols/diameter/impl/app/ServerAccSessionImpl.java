package com.mobius.software.telco.protocols.diameter.impl.app;
/*
 * Mobius Software LTD
 * Copyright 2023, Mobius Software LTD and individual contributors
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
import java.util.Collection;

import com.mobius.software.common.dal.timers.RunnableTask;
import com.mobius.software.telco.protocols.diameter.AsyncCallback;
import com.mobius.software.telco.protocols.diameter.DiameterProvider;
import com.mobius.software.telco.protocols.diameter.ResultCodes;
import com.mobius.software.telco.protocols.diameter.app.ServerAccListener;
import com.mobius.software.telco.protocols.diameter.app.ServerAccSession;
import com.mobius.software.telco.protocols.diameter.app.SessionStateEnum;
import com.mobius.software.telco.protocols.diameter.commands.DiameterAnswer;
import com.mobius.software.telco.protocols.diameter.commands.DiameterRequest;
import com.mobius.software.telco.protocols.diameter.commands.commons.AccountingAnswer;
import com.mobius.software.telco.protocols.diameter.commands.commons.AccountingRequest;
import com.mobius.software.telco.protocols.diameter.exceptions.DiameterException;
import com.mobius.software.telco.protocols.diameter.impl.DiameterSessionImpl;
import com.mobius.software.telco.protocols.diameter.primitives.common.AccountingRecordTypeEnum;
/**
*
* @author yulian oifa
*
*/
public class ServerAccSessionImpl<R1 extends AccountingRequest,A1 extends AccountingAnswer> extends DiameterSessionImpl implements ServerAccSession<A1>
{
	private DiameterProvider<?, ? extends ServerAccListener<R1, A1>, ?, ?, ?> provider;
	//for serialization
	public ServerAccSessionImpl(Long applicationID)
	{
		super(applicationID);
	}
						
	public ServerAccSessionImpl(String sessionID, Long applicationID, String remoteHost, String remoteRealm, DiameterProvider<?, ? extends ServerAccListener<R1, A1>, ?, ?, ?> provider)
	{
		super(sessionID, applicationID, remoteHost, remoteRealm, provider);
		this.provider = provider;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setProvider(DiameterProvider<?, ?, ?, ?, ?> provider)
	{
		this.provider = (DiameterProvider<?, ? extends ServerAccListener<R1, A1>, ?, ?, ?>)provider;
		super.setProvider(provider);
	}
	
	@Override
	public void sendAccountingResponse(A1 answer, AsyncCallback callback)
	{
		if(getSessionState()!=null && getSessionState()!=SessionStateEnum.IDLE && getSessionState()!=SessionStateEnum.OPEN)
		{
			callback.onError(new DiameterException("session state is invalid, can not send message", null, ResultCodes.DIAMETER_UNABLE_TO_COMPLY, null));
			return;
		}
		
		provider.getStack().getWorkerPool().addTaskLast(new RunnableTask(new Runnable()
		{	
			@Override
			public void run()
			{
				Boolean shouldKeepOpen=true;
				
				if(answer.getIsError()!=null && answer.getIsError())
				{
					shouldKeepOpen=false;
				}
				else if(answer.getAccountingRecordType()!=null && answer.getAccountingRecordType()==AccountingRecordTypeEnum.EVENT_RECORD)
				{
					shouldKeepOpen=false;
				}
				else if(answer.getAccountingRecordType()!=null && answer.getAccountingRecordType()==AccountingRecordTypeEnum.STOP_RECORD)
				{
					shouldKeepOpen=false;
				}
				
				if(!shouldKeepOpen)
				{	
					setSessionState(SessionStateEnum.IDLE);
					terminate(answer.getResultCode());
				}
				else
				{
					setSessionState(SessionStateEnum.OPEN);
					
					Long newTime = null;
					if(answer.getAcctInterimInterval()!=null)
					{
						newTime = answer.getAcctInterimInterval()*1000L;
					}
					
					answerSent(answer, newTime, callback);
				}
				
				provider.getStack().sendAnswer(answer, getRemoteHost(), getRemoteRealm(), callback);
			}
		}, this.getID()));					
	}
	
	@Override
	public void requestReceived(DiameterRequest request,String linkID, AsyncCallback callback)
	{
		try
		{
			@SuppressWarnings("unchecked")
			R1 castedRequest = (R1)request;
			super.requestReceived(request, linkID,callback);
			if(provider.getServerListeners()!=null)
			{
				@SuppressWarnings("unchecked")
				Collection<ServerAccListener<R1, A1>> listeners = (Collection<ServerAccListener<R1, A1>>) provider.getServerListeners().values();
				
				for(ServerAccListener<R1, A1> listener:listeners)
				{
					listener.onAccountingRequest(castedRequest, this, linkID,callback);
				}
			}
		}
		catch(Exception ex)
		{
			callback.onError(new DiameterException("Received unexpected request", null, ResultCodes.DIAMETER_COMMAND_UNSUPPORTED, null));
			return;
		}				
	}
	
	@Override
	public void answerReceived(DiameterAnswer answer, Long idleTime,Boolean stopSendTimer,String linkID, AsyncCallback callback)
	{
		callback.onError(new DiameterException("Received unexpected answer", null, ResultCodes.DIAMETER_COMMAND_UNSUPPORTED, null));		
	}

	@Override
	public boolean isServer()
	{
		return true;
	}
}