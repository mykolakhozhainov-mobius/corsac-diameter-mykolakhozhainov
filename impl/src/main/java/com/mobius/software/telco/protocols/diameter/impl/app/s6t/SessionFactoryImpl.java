package com.mobius.software.telco.protocols.diameter.impl.app.s6t;
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
import com.mobius.software.telco.protocols.diameter.app.s6t.ClientListener;
import com.mobius.software.telco.protocols.diameter.app.s6t.S6tClientSession;
import com.mobius.software.telco.protocols.diameter.app.s6t.S6tServerSession;
import com.mobius.software.telco.protocols.diameter.app.s6t.ServerListener;
import com.mobius.software.telco.protocols.diameter.app.s6t.SessionFactory;
import com.mobius.software.telco.protocols.diameter.commands.s6t.ReportingInformationRequest;
import com.mobius.software.telco.protocols.diameter.commands.s6t.ConfigurationInformationRequest;
import com.mobius.software.telco.protocols.diameter.commands.s6t.NIDDInformationRequest;
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
	public S6tClientSession createClientSession(ReportingInformationRequest request) throws AvpNotSupportedException
	{
		return new S6tClientSessionImpl(request.getSessionId(), request.getDestinationHost(), request.getDestinationRealm(), provider);
	}

	@Override
	public S6tServerSession createServerSession(ReportingInformationRequest request) throws AvpNotSupportedException
	{
		return new S6tServerSessionImpl(request.getSessionId(), request.getOriginHost(), request.getOriginRealm(), provider);
	}
	
	@Override
	public S6tClientSession createClientSession(ConfigurationInformationRequest request) throws AvpNotSupportedException
	{
		return new S6tClientSessionImpl(request.getSessionId(), request.getDestinationHost(), request.getDestinationRealm(), provider);
	}

	@Override
	public S6tServerSession createServerSession(ConfigurationInformationRequest request) throws AvpNotSupportedException
	{
		return new S6tServerSessionImpl(request.getSessionId(), request.getOriginHost(), request.getOriginRealm(), provider);
	}
	
	@Override
	public S6tClientSession createClientSession(NIDDInformationRequest request) throws AvpNotSupportedException
	{
		return new S6tClientSessionImpl(request.getSessionId(), request.getDestinationHost(), request.getDestinationRealm(), provider);
	}

	@Override
	public S6tServerSession createServerSession(NIDDInformationRequest request) throws AvpNotSupportedException
	{
		return new S6tServerSessionImpl(request.getSessionId(), request.getOriginHost(), request.getOriginRealm(), provider);
	}
	

}