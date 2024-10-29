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
<optgroup label="<spring:message code="engUnitGroup.acceleration"/>">
  <sst:option value="166"><spring:message code="engUnit.166"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.area"/>">
  <sst:option value="0"><spring:message code="engUnit.0"/></sst:option>
  <sst:option value="116"><spring:message code="engUnit.116"/></sst:option>
  <sst:option value="1"><spring:message code="engUnit.1"/></sst:option>
  <sst:option value="115"><spring:message code="engUnit.115"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.currency"/>">
  <sst:option value="105"><spring:message code="engUnit.105"/></sst:option>
  <sst:option value="106"><spring:message code="engUnit.106"/></sst:option>
  <sst:option value="107"><spring:message code="engUnit.107"/></sst:option>
  <sst:option value="108"><spring:message code="engUnit.108"/></sst:option>
  <sst:option value="109"><spring:message code="engUnit.109"/></sst:option>
  <sst:option value="110"><spring:message code="engUnit.110"/></sst:option>
  <sst:option value="111"><spring:message code="engUnit.111"/></sst:option>
  <sst:option value="112"><spring:message code="engUnit.112"/></sst:option>
  <sst:option value="113"><spring:message code="engUnit.113"/></sst:option>
  <sst:option value="114"><spring:message code="engUnit.114"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.electrical"/>">
  <sst:option value="2"><spring:message code="engUnit.2"/></sst:option>
  <sst:option value="3"><spring:message code="engUnit.3"/></sst:option>
  <sst:option value="167"><spring:message code="engUnit.167"/></sst:option>
  <sst:option value="168"><spring:message code="engUnit.168"/></sst:option>
  <sst:option value="169"><spring:message code="engUnit.169"/></sst:option>
  <sst:option value="170"><spring:message code="engUnit.170"/></sst:option>
  <sst:option value="171"><spring:message code="engUnit.171"/></sst:option>
  <sst:option value="4"><spring:message code="engUnit.4"/></sst:option>
  <sst:option value="172"><spring:message code="engUnit.172"/></sst:option>
  <sst:option value="145"><spring:message code="engUnit.145"/></sst:option>
  <sst:option value="122"><spring:message code="engUnit.122"/></sst:option>
  <sst:option value="123"><spring:message code="engUnit.123"/></sst:option>
  <sst:option value="173"><spring:message code="engUnit.173"/></sst:option>
  <sst:option value="174"><spring:message code="engUnit.174"/></sst:option>
  <sst:option value="175"><spring:message code="engUnit.175"/></sst:option>
  <sst:option value="5"><spring:message code="engUnit.5"/></sst:option>
  <sst:option value="124"><spring:message code="engUnit.124"/></sst:option>
  <sst:option value="6"><spring:message code="engUnit.6"/></sst:option>
  <sst:option value="7"><spring:message code="engUnit.7"/></sst:option>
  <sst:option value="8"><spring:message code="engUnit.8"/></sst:option>
  <sst:option value="9"><spring:message code="engUnit.9"/></sst:option>
  <sst:option value="10"><spring:message code="engUnit.10"/></sst:option>
  <sst:option value="11"><spring:message code="engUnit.11"/></sst:option>
  <sst:option value="12"><spring:message code="engUnit.12"/></sst:option>
  <sst:option value="13"><spring:message code="engUnit.13"/></sst:option>
  <sst:option value="176"><spring:message code="engUnit.176"/></sst:option>
  <sst:option value="177"><spring:message code="engUnit.177"/></sst:option>
  <sst:option value="14"><spring:message code="engUnit.14"/></sst:option>
  <sst:option value="15"><spring:message code="engUnit.15"/></sst:option>
  <sst:option value="178"><spring:message code="engUnit.178"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.energy"/>">
  <sst:option value="16"><spring:message code="engUnit.16"/></sst:option>
  <sst:option value="17"><spring:message code="engUnit.17"/></sst:option>
  <sst:option value="125"><spring:message code="engUnit.125"/></sst:option>
  <sst:option value="126"><spring:message code="engUnit.126"/></sst:option>
  <sst:option value="18"><spring:message code="engUnit.18"/></sst:option>
  <sst:option value="19"><spring:message code="engUnit.19"/></sst:option>
  <sst:option value="146"><spring:message code="engUnit.146"/></sst:option>
  <sst:option value="20"><spring:message code="engUnit.20"/></sst:option>
  <sst:option value="147"><spring:message code="engUnit.147"/></sst:option>
  <sst:option value="148"><spring:message code="engUnit.148"/></sst:option>
  <sst:option value="21"><spring:message code="engUnit.21"/></sst:option>
  <sst:option value="22"><spring:message code="engUnit.22"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.enthalpy"/>">
  <sst:option value="23"><spring:message code="engUnit.23"/></sst:option>
  <sst:option value="149"><spring:message code="engUnit.149"/></sst:option>
  <sst:option value="150"><spring:message code="engUnit.150"/></sst:option>
  <sst:option value="24"><spring:message code="engUnit.24"/></sst:option>
  <sst:option value="117"><spring:message code="engUnit.117"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.entropy"/>">
  <sst:option value="127"><spring:message code="engUnit.127"/></sst:option>
  <sst:option value="151"><spring:message code="engUnit.151"/></sst:option>
  <sst:option value="152"><spring:message code="engUnit.152"/></sst:option>
  <sst:option value="128"><spring:message code="engUnit.128"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.force"/>">
  <sst:option value="153"><spring:message code="engUnit.153"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.frequency"/>">
  <sst:option value="25"><spring:message code="engUnit.25"/></sst:option>
  <sst:option value="26"><spring:message code="engUnit.26"/></sst:option>
  <sst:option value="27"><spring:message code="engUnit.27"/></sst:option>
  <sst:option value="129"><spring:message code="engUnit.129"/></sst:option>
  <sst:option value="130"><spring:message code="engUnit.130"/></sst:option>
  <sst:option value="131"><spring:message code="engUnit.131"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.humidity"/>">
  <sst:option value="28"><spring:message code="engUnit.28"/></sst:option>
  <sst:option value="29"><spring:message code="engUnit.29"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.length"/>">
  <sst:option value="30"><spring:message code="engUnit.30"/></sst:option>
  <sst:option value="118"><spring:message code="engUnit.118"/></sst:option>
  <sst:option value="31"><spring:message code="engUnit.31"/></sst:option>
  <sst:option value="32"><spring:message code="engUnit.32"/></sst:option>
  <sst:option value="33"><spring:message code="engUnit.33"/></sst:option>
  <sst:option value="190"><spring:message code="engUnit.190"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.light"/>">
  <sst:option value="179"><spring:message code="engUnit.179"/></sst:option>
  <sst:option value="180"><spring:message code="engUnit.180"/></sst:option>
  <sst:option value="34"><spring:message code="engUnit.34"/></sst:option>
  <sst:option value="35"><spring:message code="engUnit.35"/></sst:option>
  <sst:option value="36"><spring:message code="engUnit.36"/></sst:option>
  <sst:option value="37"><spring:message code="engUnit.37"/></sst:option>
  <sst:option value="38"><spring:message code="engUnit.38"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.mass"/>">
  <sst:option value="39"><spring:message code="engUnit.39"/></sst:option>
  <sst:option value="40"><spring:message code="engUnit.40"/></sst:option>
  <sst:option value="41"><spring:message code="engUnit.41"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.massFlow"/>">
  <sst:option value="154"><spring:message code="engUnit.154"/></sst:option>
  <sst:option value="155"><spring:message code="engUnit.155"/></sst:option>
  <sst:option value="42"><spring:message code="engUnit.42"/></sst:option>
  <sst:option value="43"><spring:message code="engUnit.43"/></sst:option>
  <sst:option value="44"><spring:message code="engUnit.44"/></sst:option>
  <sst:option value="119"><spring:message code="engUnit.119"/></sst:option>
  <sst:option value="45"><spring:message code="engUnit.45"/></sst:option>
  <sst:option value="46"><spring:message code="engUnit.46"/></sst:option>
  <sst:option value="156"><spring:message code="engUnit.156"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.power"/>">
  <sst:option value="132"><spring:message code="engUnit.132"/></sst:option>
  <sst:option value="47"><spring:message code="engUnit.47"/></sst:option>
  <sst:option value="48"><spring:message code="engUnit.48"/></sst:option>
  <sst:option value="49"><spring:message code="engUnit.49"/></sst:option>
  <sst:option value="50"><spring:message code="engUnit.50"/></sst:option>
  <sst:option value="157"><spring:message code="engUnit.157"/></sst:option>
  <sst:option value="51"><spring:message code="engUnit.51"/></sst:option>
  <sst:option value="52"><spring:message code="engUnit.52"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.pressure"/>">
  <sst:option value="53"><spring:message code="engUnit.53"/></sst:option>
  <sst:option value="133"><spring:message code="engUnit.133"/></sst:option>
  <sst:option value="54"><spring:message code="engUnit.54"/></sst:option>
  <sst:option value="134"><spring:message code="engUnit.134"/></sst:option>
  <sst:option value="55"><spring:message code="engUnit.55"/></sst:option>
  <sst:option value="56"><spring:message code="engUnit.56"/></sst:option>
  <sst:option value="57"><spring:message code="engUnit.57"/></sst:option>
  <sst:option value="58"><spring:message code="engUnit.58"/></sst:option>
  <sst:option value="59"><spring:message code="engUnit.59"/></sst:option>
  <sst:option value="60"><spring:message code="engUnit.60"/></sst:option>
  <sst:option value="61"><spring:message code="engUnit.61"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.temperature"/>">
  <sst:option value="62"><spring:message code="engUnit.62"/></sst:option>
  <sst:option value="63"><spring:message code="engUnit.63"/></sst:option>
  <sst:option value="181"><spring:message code="engUnit.181"/></sst:option>
  <sst:option value="182"><spring:message code="engUnit.182"/></sst:option>
  <sst:option value="64"><spring:message code="engUnit.64"/></sst:option>
  <sst:option value="65"><spring:message code="engUnit.65"/></sst:option>
  <sst:option value="66"><spring:message code="engUnit.66"/></sst:option>
  <sst:option value="120"><spring:message code="engUnit.120"/></sst:option>
  <sst:option value="121"><spring:message code="engUnit.121"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.time"/>">
  <sst:option value="67"><spring:message code="engUnit.67"/></sst:option>
  <sst:option value="68"><spring:message code="engUnit.68"/></sst:option>
  <sst:option value="69"><spring:message code="engUnit.69"/></sst:option>
  <sst:option value="70"><spring:message code="engUnit.70"/></sst:option>
  <sst:option value="71"><spring:message code="engUnit.71"/></sst:option>
  <sst:option value="72"><spring:message code="engUnit.72"/></sst:option>
  <sst:option value="73"><spring:message code="engUnit.73"/></sst:option>
  <sst:option value="158"><spring:message code="engUnit.158"/></sst:option>
  <sst:option value="159"><spring:message code="engUnit.159"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.torque"/>">
  <sst:option value="160"><spring:message code="engUnit.160"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.velocity"/>">
  <sst:option value="161"><spring:message code="engUnit.161"/></sst:option>
  <sst:option value="162"><spring:message code="engUnit.162"/></sst:option>
  <sst:option value="74"><spring:message code="engUnit.74"/></sst:option>
  <sst:option value="163"><spring:message code="engUnit.163"/></sst:option>
  <sst:option value="164"><spring:message code="engUnit.164"/></sst:option>
  <sst:option value="165"><spring:message code="engUnit.165"/></sst:option>
  <sst:option value="76"><spring:message code="engUnit.76"/></sst:option>
  <sst:option value="77"><spring:message code="engUnit.77"/></sst:option>
  <sst:option value="78"><spring:message code="engUnit.78"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.volume"/>">
  <sst:option value="79"><spring:message code="engUnit.79"/></sst:option>
  <sst:option value="80"><spring:message code="engUnit.80"/></sst:option>
  <sst:option value="81"><spring:message code="engUnit.81"/></sst:option>
  <sst:option value="82"><spring:message code="engUnit.82"/></sst:option>
  <sst:option value="83"><spring:message code="engUnit.83"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.volumetricFlow"/>">
  <sst:option value="142"><spring:message code="engUnit.142"/></sst:option>
  <sst:option value="84"><spring:message code="engUnit.84"/></sst:option>
  <sst:option value="85"><spring:message code="engUnit.85"/></sst:option>
  <sst:option value="165"><spring:message code="engUnit.165"/></sst:option>
  <sst:option value="135"><spring:message code="engUnit.135"/></sst:option>
  <sst:option value="86"><spring:message code="engUnit.86"/></sst:option>
  <sst:option value="87"><spring:message code="engUnit.87"/></sst:option>
  <sst:option value="88"><spring:message code="engUnit.88"/></sst:option>
  <sst:option value="136"><spring:message code="engUnit.136"/></sst:option>
  <sst:option value="89"><spring:message code="engUnit.89"/></sst:option>
</optgroup>
<optgroup label="<spring:message code="engUnitGroup.other"/>">
  <sst:option value="90"><spring:message code="engUnit.90"/></sst:option>
  <sst:option value="91"><spring:message code="engUnit.91"/></sst:option>
  <sst:option value="92"><spring:message code="engUnit.92"/></sst:option>
  <sst:option value="93"><spring:message code="engUnit.93"/></sst:option>
  <sst:option value="94"><spring:message code="engUnit.94"/></sst:option>
  <sst:option value="183"><spring:message code="engUnit.183"/></sst:option>
  <sst:option value="186"><spring:message code="engUnit.186"/></sst:option>
  <sst:option value="137"><spring:message code="engUnit.137"/></sst:option>
  <sst:option value="138"><spring:message code="engUnit.138"/></sst:option>
  <sst:option value="139"><spring:message code="engUnit.139"/></sst:option>
  <sst:option value="140"><spring:message code="engUnit.140"/></sst:option>
  <sst:option value="95"><spring:message code="engUnit.95"/></sst:option>
  <sst:option value="187"><spring:message code="engUnit.187"/></sst:option>
  <sst:option value="188"><spring:message code="engUnit.188"/></sst:option>
  <sst:option value="96"><spring:message code="engUnit.96"/></sst:option>
  <sst:option value="97"><spring:message code="engUnit.97"/></sst:option>
  <sst:option value="98"><spring:message code="engUnit.98"/></sst:option>
  <sst:option value="143"><spring:message code="engUnit.143"/></sst:option>
  <sst:option value="144"><spring:message code="engUnit.144"/></sst:option>
  <sst:option value="99"><spring:message code="engUnit.99"/></sst:option>
  <sst:option value="100"><spring:message code="engUnit.100"/></sst:option>
  <sst:option value="101"><spring:message code="engUnit.101"/></sst:option>
  <sst:option value="102"><spring:message code="engUnit.102"/></sst:option>
  <sst:option value="103"><spring:message code="engUnit.103"/></sst:option>
  <sst:option value="184"><spring:message code="engUnit.184"/></sst:option>
  <sst:option value="104"><spring:message code="engUnit.104"/></sst:option>
  <sst:option value="185"><spring:message code="engUnit.185"/></sst:option>
  <sst:option value="189"><spring:message code="engUnit.189"/></sst:option>
  <sst:option value="141"><spring:message code="engUnit.141"/></sst:option>
</optgroup>