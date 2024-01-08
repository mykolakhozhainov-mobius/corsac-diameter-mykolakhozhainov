package com.mobius.software.telco.protocols.diameter.impl.commands.sy;

import com.mobius.software.telco.protocols.diameter.commands.sy.SyRequest;
import com.mobius.software.telco.protocols.diameter.impl.commands.common.AuthenticationRequestWithHostBase;
import com.mobius.software.telco.protocols.diameter.impl.primitives.rfc7944.DRMPImpl;
import com.mobius.software.telco.protocols.diameter.primitives.rfc7683.OCSupportedFeatures;
import com.mobius.software.telco.protocols.diameter.primitives.rfc7944.DRMP;
import com.mobius.software.telco.protocols.diameter.primitives.rfc7944.DRMPEnum;

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
public abstract class SyRequestImpl extends AuthenticationRequestWithHostBase implements SyRequest
{
	protected DRMP drmp;
	
	protected OCSupportedFeatures ocSupportedFeatures;
	
	protected SyRequestImpl() 
	{
		super();
		setDestinationHostAllowed(true);
	}
		
	public SyRequestImpl(String originHost,String originRealm,String destinationHost, String destinationRealm,Boolean isRetransmit, String sessonID, Long authApplicationId)
	{
		super(originHost, originRealm, destinationHost, destinationRealm, isRetransmit, sessonID, authApplicationId);
		setDestinationHostAllowed(true);
	}

	@Override
	public DRMPEnum getDRMP() 
	{
		if(drmp==null)
			return null;
		
		return drmp.getEnumerated(DRMPEnum.class);
	}

	@Override
	public void setDRMP(DRMPEnum value) 
	{
		if(value==null)
			this.drmp = null;
		else
			this.drmp = new DRMPImpl(value, null, null);
	}

	@Override
	public OCSupportedFeatures getOCSupportedFeatures() 
	{
		return ocSupportedFeatures;
	}

	@Override
	public void setOCSupportedFeatures(OCSupportedFeatures value) 
	{
		this.ocSupportedFeatures = value;
	}
}