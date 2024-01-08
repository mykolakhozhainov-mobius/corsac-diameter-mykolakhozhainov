package com.mobius.software.telco.protocols.diameter.impl.commands.common;

import java.util.ArrayList;
import java.util.List;

import com.mobius.software.telco.protocols.diameter.annotations.DiameterCommandImplementation;
import com.mobius.software.telco.protocols.diameter.annotations.DiameterOrder;
import com.mobius.software.telco.protocols.diameter.commands.commons.DisconnectPeerAnswer;
import com.mobius.software.telco.protocols.diameter.impl.commands.DiameterAnswerBase;
import com.mobius.software.telco.protocols.diameter.primitives.DiameterAvp;

/*
 * Mobius Software LTD, Open Source Cloud Communications
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

/**
*
* @author yulian oifa
*
*/
@DiameterCommandImplementation(applicationId = 0, commandCode = 282, request = false)
public class DisconnectPeerAnswerImpl extends DiameterAnswerBase implements DisconnectPeerAnswer
{
	protected DisconnectPeerAnswerImpl() 
	{
		super();
		setSessionIdAllowed(false);
		setProxyInfoAllowed(false);
		setUsernameAllowed(false);
		setOriginStateIdAllowedAllowed(false);
	}
	
	public DisconnectPeerAnswerImpl(String originHost,String originRealm,Boolean isRetransmit, Long resultCode)
	{
		super(originHost, originRealm, isRetransmit, resultCode);
		setSessionIdAllowed(false);
		setProxyInfoAllowed(false);
		setUsernameAllowed(false);
		setOriginStateIdAllowedAllowed(false);
	}
	
	@DiameterOrder
	public List<DiameterAvp> getOrderedAVPs()
	{
		List<DiameterAvp> result=new ArrayList<DiameterAvp>();
		result.add(resultCode);
		result.add(originHost);
		result.add(originRealm);
		result.add(errorMessage);
		result.add(failedAvp);
		
		if(optionalAvps!=null)
		{
			for(List<DiameterAvp> curr:optionalAvps.values())
				result.addAll(curr);
		}
		
		return result;
	}
}