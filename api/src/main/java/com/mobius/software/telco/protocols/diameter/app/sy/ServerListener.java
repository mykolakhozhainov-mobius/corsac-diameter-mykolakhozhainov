package com.mobius.software.telco.protocols.diameter.app.sy;

import com.mobius.software.telco.protocols.diameter.AsyncCallback;
import com.mobius.software.telco.protocols.diameter.app.ServerAuthListener;
import com.mobius.software.telco.protocols.diameter.app.ServerAuthSession;
import com.mobius.software.telco.protocols.diameter.commands.sy.AbortSessionAnswer;
import com.mobius.software.telco.protocols.diameter.commands.sy.AbortSessionRequest;
import com.mobius.software.telco.protocols.diameter.commands.sy.ReAuthAnswer;
import com.mobius.software.telco.protocols.diameter.commands.sy.ReAuthRequest;
import com.mobius.software.telco.protocols.diameter.commands.sy.SessionTerminationAnswer;
import com.mobius.software.telco.protocols.diameter.commands.sy.SessionTerminationRequest;
import com.mobius.software.telco.protocols.diameter.commands.sy.SpendingLimitAnswer;
import com.mobius.software.telco.protocols.diameter.commands.sy.SpendingLimitRequest;
import com.mobius.software.telco.protocols.diameter.commands.sy.SpendingStatusNotificationAnswer;
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
public interface ServerListener extends ServerAuthListener<SpendingLimitRequest, SpendingLimitAnswer, ReAuthRequest, ReAuthAnswer , AbortSessionRequest, AbortSessionAnswer, SessionTerminationRequest, SessionTerminationAnswer>
{
	void onSpendingStatusNotificationAnswer(SpendingStatusNotificationAnswer answer,ServerAuthSession<SpendingLimitAnswer,ReAuthRequest,AbortSessionRequest,SessionTerminationAnswer> session,String linkID,AsyncCallback callback);		
}