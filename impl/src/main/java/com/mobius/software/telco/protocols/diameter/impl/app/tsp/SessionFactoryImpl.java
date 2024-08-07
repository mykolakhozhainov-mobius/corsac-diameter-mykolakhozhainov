package com.mobius.software.telco.protocols.diameter.impl.app.tsp;
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

import com.mobius.software.telco.protocols.diameter.DiameterProvider;
import com.mobius.software.telco.protocols.diameter.app.tsp.ClientListener;
import com.mobius.software.telco.protocols.diameter.app.tsp.ServerListener;
import com.mobius.software.telco.protocols.diameter.app.tsp.SessionFactory;
import com.mobius.software.telco.protocols.diameter.app.tsp.TspClientSession;
import com.mobius.software.telco.protocols.diameter.app.tsp.TspServerSession;
import com.mobius.software.telco.protocols.diameter.commands.tsp.DeviceNotificationRequest;
import com.mobius.software.telco.protocols.diameter.commands.tsp.DeviceActionRequest;
import com.mobius.software.telco.protocols.diameter.exceptions.AvpNotSupportedException;
/**
*
* @author yulian oifa
*
*/
public class SessionFactoryImpl implements SessionFactory
{
	private DiameterProvider<ClientListener,ServerListener,?, ?, ?> provider;
	
	public SessionFactoryImpl(DiameterProvider<ClientListener,ServerListener,?, ?, ?> provider)
	{
		this.provider = provider;
	}

	@Override
	public TspClientSession createClientSession(DeviceNotificationRequest request) throws AvpNotSupportedException
	{
		return new TspClientSessionImpl(request.getSessionId(), request.getDestinationHost(), request.getDestinationRealm(), provider);
	}

	@Override
	public TspServerSession createServerSession(DeviceNotificationRequest request) throws AvpNotSupportedException
	{
		return new TspServerSessionImpl(request.getSessionId(), request.getOriginHost(), request.getOriginRealm(), provider);
	}
	
	@Override
	public TspClientSession createClientSession(DeviceActionRequest request) throws AvpNotSupportedException
	{
		return new TspClientSessionImpl(request.getSessionId(), request.getDestinationHost(), request.getDestinationRealm(), provider);
	}

	@Override
	public TspServerSession createServerSession(DeviceActionRequest request) throws AvpNotSupportedException
	{
		return new TspServerSessionImpl(request.getSessionId(), request.getOriginHost(), request.getOriginRealm(), provider);
	}
	
	
	
}