package com.mobius.software.telco.protocols.diameter.impl.commands.s7a;

import java.util.ArrayList;
import java.util.List;

import com.mobius.software.telco.protocols.diameter.annotations.DiameterCommandImplementation;
import com.mobius.software.telco.protocols.diameter.annotations.DiameterOrder;
import com.mobius.software.telco.protocols.diameter.annotations.DiameterValidate;
import com.mobius.software.telco.protocols.diameter.commands.s7a.InsertSubscriberDataRequest;
import com.mobius.software.telco.protocols.diameter.impl.primitives.s6a.ResetIDImpl;
import com.mobius.software.telco.protocols.diameter.primitives.DiameterAvp;
import com.mobius.software.telco.protocols.diameter.primitives.common.AuthSessionStateEnum;
import com.mobius.software.telco.protocols.diameter.primitives.s6a.ResetID;
import com.mobius.software.telco.protocols.diameter.primitives.s6a.VPLMNCSGSubscriptionData;

import io.netty.buffer.ByteBuf;

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
@DiameterCommandImplementation(applicationId = 16777308, commandCode = 319, request = true)
public class InsertSubscriberDataRequestImpl extends S7aRequestImpl implements InsertSubscriberDataRequest
{
	private List<VPLMNCSGSubscriptionData> vplmnCSGSubscriptionData;
	
	private List<ResetID> resetID;
	
	protected InsertSubscriberDataRequestImpl() 
	{
		super();
	}
	
	public InsertSubscriberDataRequestImpl(String originHost,String originRealm,String destinationHost,String destinationRealm,Boolean isRetransmit, String sessionID,AuthSessionStateEnum authSessionState,List<VPLMNCSGSubscriptionData> vplmnCSGSubscriptionData)
	{
		super(originHost, originRealm, destinationHost, destinationRealm, isRetransmit, sessionID, authSessionState);
		
		setVPLMNCSGSubscriptionData(vplmnCSGSubscriptionData);
	}
	
	@Override
	public List<VPLMNCSGSubscriptionData> getVPLMNCSGSubscriptionData()
	{
		return this.vplmnCSGSubscriptionData;
	}
	
	@Override
	public void setVPLMNCSGSubscriptionData(List<VPLMNCSGSubscriptionData> value)
	{
		if(value == null || value.size()==0)
			throw new IllegalArgumentException("VPLMN-CSG-Subscription-Data is required");
		
		this.vplmnCSGSubscriptionData = value;
	}
	
	@Override
	public List<ByteBuf> getResetID()
	{
		if(resetID==null || resetID.size()==0)
			return null;
		
		List<ByteBuf> result = new ArrayList<ByteBuf>();
		for(ResetID curr: resetID)
			result.add(curr.getValue());
		
		return result;
	}
	 
	@Override
	public void setResetID(List<ByteBuf> value)
	{
		if(value==null || value.size()==0)
			this.resetID = null;
		else
		{	
			this.resetID = new ArrayList<ResetID>();
			for(ByteBuf curr:value)
				this.resetID.add(new ResetIDImpl(curr, null, null));
		}
	}
	
	@DiameterValidate
	public String validate()
	{
		if(vplmnCSGSubscriptionData == null || vplmnCSGSubscriptionData.size()==0)
			return "VPLMN-CSG-Subscription-Data is required";
		
		return super.validate();
	}		
	
	@DiameterOrder
	public List<DiameterAvp> getOrderedAVPs()
	{
		List<DiameterAvp> result=new ArrayList<DiameterAvp>();
		result.add(sessionId);
		result.add(drmp);
		result.add(vendorSpecificApplicationId);
		result.add(authSessionState);
		result.add(originHost);
		result.add(originRealm);
		result.add(destinationHost);
		result.add(destinationRealm);
		result.add(username);
		
		if(supportedFeatures!=null)
			result.addAll(supportedFeatures);
		
		if(vplmnCSGSubscriptionData!=null)
			result.addAll(vplmnCSGSubscriptionData);
		
		if(resetID!=null)
			result.addAll(resetID);
		
		if(optionalAvps!=null)
		{
			for(List<DiameterAvp> curr:optionalAvps.values())
				result.addAll(curr);
		}
		
		if(proxyInfo!=null)
			result.addAll(proxyInfo);
		
		if(routeRecords!=null)
			result.addAll(routeRecords);
		
		return result;
	}		
}