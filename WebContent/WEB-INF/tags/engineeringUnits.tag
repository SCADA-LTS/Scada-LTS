<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/.
--%><%@include file="/WEB-INF/tags/decl.tagf"%><%--
--%><%@tag import="org.joda.time.DateTimeConstants"%><%--
--%><%@tag body-content="empty"%>
<optgroup label="<fmt:message key="engUnitGroup.acceleration"/>">
  <sst:option value="166"><fmt:message key="engUnit.166"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.area"/>">
  <sst:option value="0"><fmt:message key="engUnit.0"/></sst:option>
  <sst:option value="116"><fmt:message key="engUnit.116"/></sst:option>
  <sst:option value="1"><fmt:message key="engUnit.1"/></sst:option>
  <sst:option value="115"><fmt:message key="engUnit.115"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.currency"/>">
  <sst:option value="105"><fmt:message key="engUnit.105"/></sst:option>
  <sst:option value="106"><fmt:message key="engUnit.106"/></sst:option>
  <sst:option value="107"><fmt:message key="engUnit.107"/></sst:option>
  <sst:option value="108"><fmt:message key="engUnit.108"/></sst:option>
  <sst:option value="109"><fmt:message key="engUnit.109"/></sst:option>
  <sst:option value="110"><fmt:message key="engUnit.110"/></sst:option>
  <sst:option value="111"><fmt:message key="engUnit.111"/></sst:option>
  <sst:option value="112"><fmt:message key="engUnit.112"/></sst:option>
  <sst:option value="113"><fmt:message key="engUnit.113"/></sst:option>
  <sst:option value="114"><fmt:message key="engUnit.114"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.electrical"/>">
  <sst:option value="2"><fmt:message key="engUnit.2"/></sst:option>
  <sst:option value="3"><fmt:message key="engUnit.3"/></sst:option>
  <sst:option value="167"><fmt:message key="engUnit.167"/></sst:option>
  <sst:option value="168"><fmt:message key="engUnit.168"/></sst:option>
  <sst:option value="169"><fmt:message key="engUnit.169"/></sst:option>
  <sst:option value="170"><fmt:message key="engUnit.170"/></sst:option>
  <sst:option value="171"><fmt:message key="engUnit.171"/></sst:option>
  <sst:option value="4"><fmt:message key="engUnit.4"/></sst:option>
  <sst:option value="172"><fmt:message key="engUnit.172"/></sst:option>
  <sst:option value="145"><fmt:message key="engUnit.145"/></sst:option>
  <sst:option value="122"><fmt:message key="engUnit.122"/></sst:option>
  <sst:option value="123"><fmt:message key="engUnit.123"/></sst:option>
  <sst:option value="173"><fmt:message key="engUnit.173"/></sst:option>
  <sst:option value="174"><fmt:message key="engUnit.174"/></sst:option>
  <sst:option value="175"><fmt:message key="engUnit.175"/></sst:option>
  <sst:option value="5"><fmt:message key="engUnit.5"/></sst:option>
  <sst:option value="124"><fmt:message key="engUnit.124"/></sst:option>
  <sst:option value="6"><fmt:message key="engUnit.6"/></sst:option>
  <sst:option value="7"><fmt:message key="engUnit.7"/></sst:option>
  <sst:option value="8"><fmt:message key="engUnit.8"/></sst:option>
  <sst:option value="9"><fmt:message key="engUnit.9"/></sst:option>
  <sst:option value="10"><fmt:message key="engUnit.10"/></sst:option>
  <sst:option value="11"><fmt:message key="engUnit.11"/></sst:option>
  <sst:option value="12"><fmt:message key="engUnit.12"/></sst:option>
  <sst:option value="13"><fmt:message key="engUnit.13"/></sst:option>
  <sst:option value="176"><fmt:message key="engUnit.176"/></sst:option>
  <sst:option value="177"><fmt:message key="engUnit.177"/></sst:option>
  <sst:option value="14"><fmt:message key="engUnit.14"/></sst:option>
  <sst:option value="15"><fmt:message key="engUnit.15"/></sst:option>
  <sst:option value="178"><fmt:message key="engUnit.178"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.energy"/>">
  <sst:option value="16"><fmt:message key="engUnit.16"/></sst:option>
  <sst:option value="17"><fmt:message key="engUnit.17"/></sst:option>
  <sst:option value="125"><fmt:message key="engUnit.125"/></sst:option>
  <sst:option value="126"><fmt:message key="engUnit.126"/></sst:option>
  <sst:option value="18"><fmt:message key="engUnit.18"/></sst:option>
  <sst:option value="19"><fmt:message key="engUnit.19"/></sst:option>
  <sst:option value="146"><fmt:message key="engUnit.146"/></sst:option>
  <sst:option value="20"><fmt:message key="engUnit.20"/></sst:option>
  <sst:option value="147"><fmt:message key="engUnit.147"/></sst:option>
  <sst:option value="148"><fmt:message key="engUnit.148"/></sst:option>
  <sst:option value="21"><fmt:message key="engUnit.21"/></sst:option>
  <sst:option value="22"><fmt:message key="engUnit.22"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.enthalpy"/>">
  <sst:option value="23"><fmt:message key="engUnit.23"/></sst:option>
  <sst:option value="149"><fmt:message key="engUnit.149"/></sst:option>
  <sst:option value="150"><fmt:message key="engUnit.150"/></sst:option>
  <sst:option value="24"><fmt:message key="engUnit.24"/></sst:option>
  <sst:option value="117"><fmt:message key="engUnit.117"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.entropy"/>">
  <sst:option value="127"><fmt:message key="engUnit.127"/></sst:option>
  <sst:option value="151"><fmt:message key="engUnit.151"/></sst:option>
  <sst:option value="152"><fmt:message key="engUnit.152"/></sst:option>
  <sst:option value="128"><fmt:message key="engUnit.128"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.force"/>">
  <sst:option value="153"><fmt:message key="engUnit.153"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.frequency"/>">
  <sst:option value="25"><fmt:message key="engUnit.25"/></sst:option>
  <sst:option value="26"><fmt:message key="engUnit.26"/></sst:option>
  <sst:option value="27"><fmt:message key="engUnit.27"/></sst:option>
  <sst:option value="129"><fmt:message key="engUnit.129"/></sst:option>
  <sst:option value="130"><fmt:message key="engUnit.130"/></sst:option>
  <sst:option value="131"><fmt:message key="engUnit.131"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.humidity"/>">
  <sst:option value="28"><fmt:message key="engUnit.28"/></sst:option>
  <sst:option value="29"><fmt:message key="engUnit.29"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.length"/>">
  <sst:option value="30"><fmt:message key="engUnit.30"/></sst:option>
  <sst:option value="118"><fmt:message key="engUnit.118"/></sst:option>
  <sst:option value="31"><fmt:message key="engUnit.31"/></sst:option>
  <sst:option value="32"><fmt:message key="engUnit.32"/></sst:option>
  <sst:option value="33"><fmt:message key="engUnit.33"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.light"/>">
  <sst:option value="179"><fmt:message key="engUnit.179"/></sst:option>
  <sst:option value="180"><fmt:message key="engUnit.180"/></sst:option>
  <sst:option value="34"><fmt:message key="engUnit.34"/></sst:option>
  <sst:option value="35"><fmt:message key="engUnit.35"/></sst:option>
  <sst:option value="36"><fmt:message key="engUnit.36"/></sst:option>
  <sst:option value="37"><fmt:message key="engUnit.37"/></sst:option>
  <sst:option value="38"><fmt:message key="engUnit.38"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.mass"/>">
  <sst:option value="39"><fmt:message key="engUnit.39"/></sst:option>
  <sst:option value="40"><fmt:message key="engUnit.40"/></sst:option>
  <sst:option value="41"><fmt:message key="engUnit.41"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.massFlow"/>">
  <sst:option value="154"><fmt:message key="engUnit.154"/></sst:option>
  <sst:option value="155"><fmt:message key="engUnit.155"/></sst:option>
  <sst:option value="42"><fmt:message key="engUnit.42"/></sst:option>
  <sst:option value="43"><fmt:message key="engUnit.43"/></sst:option>
  <sst:option value="44"><fmt:message key="engUnit.44"/></sst:option>
  <sst:option value="119"><fmt:message key="engUnit.119"/></sst:option>
  <sst:option value="45"><fmt:message key="engUnit.45"/></sst:option>
  <sst:option value="46"><fmt:message key="engUnit.46"/></sst:option>
  <sst:option value="156"><fmt:message key="engUnit.156"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.power"/>">
  <sst:option value="132"><fmt:message key="engUnit.132"/></sst:option>
  <sst:option value="47"><fmt:message key="engUnit.47"/></sst:option>
  <sst:option value="48"><fmt:message key="engUnit.48"/></sst:option>
  <sst:option value="49"><fmt:message key="engUnit.49"/></sst:option>
  <sst:option value="50"><fmt:message key="engUnit.50"/></sst:option>
  <sst:option value="157"><fmt:message key="engUnit.157"/></sst:option>
  <sst:option value="51"><fmt:message key="engUnit.51"/></sst:option>
  <sst:option value="52"><fmt:message key="engUnit.52"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.pressure"/>">
  <sst:option value="53"><fmt:message key="engUnit.53"/></sst:option>
  <sst:option value="133"><fmt:message key="engUnit.133"/></sst:option>
  <sst:option value="54"><fmt:message key="engUnit.54"/></sst:option>
  <sst:option value="134"><fmt:message key="engUnit.134"/></sst:option>
  <sst:option value="55"><fmt:message key="engUnit.55"/></sst:option>
  <sst:option value="56"><fmt:message key="engUnit.56"/></sst:option>
  <sst:option value="57"><fmt:message key="engUnit.57"/></sst:option>
  <sst:option value="58"><fmt:message key="engUnit.58"/></sst:option>
  <sst:option value="59"><fmt:message key="engUnit.59"/></sst:option>
  <sst:option value="60"><fmt:message key="engUnit.60"/></sst:option>
  <sst:option value="61"><fmt:message key="engUnit.61"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.temperature"/>">
  <sst:option value="62"><fmt:message key="engUnit.62"/></sst:option>
  <sst:option value="63"><fmt:message key="engUnit.63"/></sst:option>
  <sst:option value="181"><fmt:message key="engUnit.181"/></sst:option>
  <sst:option value="182"><fmt:message key="engUnit.182"/></sst:option>
  <sst:option value="64"><fmt:message key="engUnit.64"/></sst:option>
  <sst:option value="65"><fmt:message key="engUnit.65"/></sst:option>
  <sst:option value="66"><fmt:message key="engUnit.66"/></sst:option>
  <sst:option value="120"><fmt:message key="engUnit.120"/></sst:option>
  <sst:option value="121"><fmt:message key="engUnit.121"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.time"/>">
  <sst:option value="67"><fmt:message key="engUnit.67"/></sst:option>
  <sst:option value="68"><fmt:message key="engUnit.68"/></sst:option>
  <sst:option value="69"><fmt:message key="engUnit.69"/></sst:option>
  <sst:option value="70"><fmt:message key="engUnit.70"/></sst:option>
  <sst:option value="71"><fmt:message key="engUnit.71"/></sst:option>
  <sst:option value="72"><fmt:message key="engUnit.72"/></sst:option>
  <sst:option value="73"><fmt:message key="engUnit.73"/></sst:option>
  <sst:option value="158"><fmt:message key="engUnit.158"/></sst:option>
  <sst:option value="159"><fmt:message key="engUnit.159"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.torque"/>">
  <sst:option value="160"><fmt:message key="engUnit.160"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.velocity"/>">
  <sst:option value="161"><fmt:message key="engUnit.161"/></sst:option>
  <sst:option value="162"><fmt:message key="engUnit.162"/></sst:option>
  <sst:option value="74"><fmt:message key="engUnit.74"/></sst:option>
  <sst:option value="163"><fmt:message key="engUnit.163"/></sst:option>
  <sst:option value="164"><fmt:message key="engUnit.164"/></sst:option>
  <sst:option value="165"><fmt:message key="engUnit.165"/></sst:option>
  <sst:option value="76"><fmt:message key="engUnit.76"/></sst:option>
  <sst:option value="77"><fmt:message key="engUnit.77"/></sst:option>
  <sst:option value="78"><fmt:message key="engUnit.78"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.volume"/>">
  <sst:option value="79"><fmt:message key="engUnit.79"/></sst:option>
  <sst:option value="80"><fmt:message key="engUnit.80"/></sst:option>
  <sst:option value="81"><fmt:message key="engUnit.81"/></sst:option>
  <sst:option value="82"><fmt:message key="engUnit.82"/></sst:option>
  <sst:option value="83"><fmt:message key="engUnit.83"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.volumetricFlow"/>">
  <sst:option value="142"><fmt:message key="engUnit.142"/></sst:option>
  <sst:option value="84"><fmt:message key="engUnit.84"/></sst:option>
  <sst:option value="85"><fmt:message key="engUnit.85"/></sst:option>
  <sst:option value="165"><fmt:message key="engUnit.165"/></sst:option>
  <sst:option value="135"><fmt:message key="engUnit.135"/></sst:option>
  <sst:option value="86"><fmt:message key="engUnit.86"/></sst:option>
  <sst:option value="87"><fmt:message key="engUnit.87"/></sst:option>
  <sst:option value="88"><fmt:message key="engUnit.88"/></sst:option>
  <sst:option value="136"><fmt:message key="engUnit.136"/></sst:option>
  <sst:option value="89"><fmt:message key="engUnit.89"/></sst:option>
</optgroup>
<optgroup label="<fmt:message key="engUnitGroup.other"/>">
  <sst:option value="90"><fmt:message key="engUnit.90"/></sst:option>
  <sst:option value="91"><fmt:message key="engUnit.91"/></sst:option>
  <sst:option value="92"><fmt:message key="engUnit.92"/></sst:option>
  <sst:option value="93"><fmt:message key="engUnit.93"/></sst:option>
  <sst:option value="94"><fmt:message key="engUnit.94"/></sst:option>
  <sst:option value="183"><fmt:message key="engUnit.183"/></sst:option>
  <sst:option value="186"><fmt:message key="engUnit.186"/></sst:option>
  <sst:option value="137"><fmt:message key="engUnit.137"/></sst:option>
  <sst:option value="138"><fmt:message key="engUnit.138"/></sst:option>
  <sst:option value="139"><fmt:message key="engUnit.139"/></sst:option>
  <sst:option value="140"><fmt:message key="engUnit.140"/></sst:option>
  <sst:option value="95"><fmt:message key="engUnit.95"/></sst:option>
  <sst:option value="187"><fmt:message key="engUnit.187"/></sst:option>
  <sst:option value="188"><fmt:message key="engUnit.188"/></sst:option>
  <sst:option value="96"><fmt:message key="engUnit.96"/></sst:option>
  <sst:option value="97"><fmt:message key="engUnit.97"/></sst:option>
  <sst:option value="98"><fmt:message key="engUnit.98"/></sst:option>
  <sst:option value="143"><fmt:message key="engUnit.143"/></sst:option>
  <sst:option value="144"><fmt:message key="engUnit.144"/></sst:option>
  <sst:option value="99"><fmt:message key="engUnit.99"/></sst:option>
  <sst:option value="100"><fmt:message key="engUnit.100"/></sst:option>
  <sst:option value="101"><fmt:message key="engUnit.101"/></sst:option>
  <sst:option value="102"><fmt:message key="engUnit.102"/></sst:option>
  <sst:option value="103"><fmt:message key="engUnit.103"/></sst:option>
  <sst:option value="184"><fmt:message key="engUnit.184"/></sst:option>
  <sst:option value="104"><fmt:message key="engUnit.104"/></sst:option>
  <sst:option value="185"><fmt:message key="engUnit.185"/></sst:option>
  <sst:option value="189"><fmt:message key="engUnit.189"/></sst:option>
  <sst:option value="141"><fmt:message key="engUnit.141"/></sst:option>
</optgroup>