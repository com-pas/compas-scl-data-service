/**
 * SPDX-FileCopyrightText: 2021 Alliander N.V.
 *
 * SPDX-License-Identifier: Apache-2.0
*/

--
-- Insert LNode library into scl_file table
--
insert into scl_file(
    id, major_version, minor_version, patch_version, type, name, created_by, scl_data)
    values (
        'fc55c46d-c109-4ccd-bf66-9f1d0e135689',
        1,
        0,
        0,
        'SSD',
        'LNodeTypeLibrary',
        'system',
        '<?xml version="1.0" encoding="UTF-8"?>
<SCL xmlns="http://www.iec.ch/61850/2003/SCL" version="2007" revision="B" release="4" xmlns:compas="https://www.lfenergy.org/compas/extension/v1">
	<Private type="compas_scl">
		<compas:Labels/>
	</Private>
	<Header id="LNodeTypeBasic"/>
	<DataTypeTemplates>
		<LNodeType lnClass="TVTR" desc="Voltage Transformer" id="TVTR$oscd$_a0be960c8dfd3708">
			<DO name="Beh" type="Beh$oscd$_c6ed035c8137b35a"/>
			<DO name="HzRtg" type="HzRtg$oscd$_70f83e1c005744f2"/>
			<DO name="VRtg" type="VRtg$oscd$_70f83e1c005744f2"/>
			<DO name="VolSv" type="VolSv$oscd$_ff92aedee5482392"/>
		</LNodeType>
		<LNodeType lnClass="TCTR" desc="Current Transformer" id="TCTR$oscd$_defaa767081f017d">
			<DO name="ARtg" type="ARtg$oscd$_70f83e1c005744f2"/>
			<DO name="Beh" type="Beh$oscd$_c6ed035c8137b35a"/>
			<DO name="HzRtg" type="HzRtg$oscd$_70f83e1c005744f2"/>
			<DO name="AmpSv" type="AmpSv$oscd$_ff92aedee5482392"/>
		</LNodeType>
		<LNodeType lnClass="XSWI" desc="Switch" id="XSWI$oscd$_74c3c9de7d5cdfad">
			<DO name="Beh" type="Beh$oscd$_c6ed035c8137b35a"/>
			<DO name="BlkCls" type="BlkCls$oscd$_637b3bee56ce66e4"/>
			<DO name="BlkOpn" type="BlkOpn$oscd$_637b3bee56ce66e4"/>
			<DO name="Loc" type="Loc$oscd$_d915d66d9e42a575"/>
			<DO name="OpCnt" type="OpCnt$oscd$_bbaa9369107884bc"/>
			<DO name="Pos" type="Pos$oscd$_fb08409822e126d6"/>
			<DO name="SwTyp" type="SwTyp$oscd$_082f086f651ace0d"/>
		</LNodeType>
		<LNodeType lnClass="XCBR" desc="Circuit Breaker" id="XCBR$oscd$_b8418061c0b79b58">
			<DO name="Beh" type="Beh$oscd$_c6ed035c8137b35a"/>
			<DO name="BlkCls" type="BlkCls$oscd$_637b3bee56ce66e4"/>
			<DO name="BlkOpn" type="BlkOpn$oscd$_637b3bee56ce66e4"/>
			<DO name="Loc" type="Loc$oscd$_d915d66d9e42a575"/>
			<DO name="OpCnt" type="OpCnt$oscd$_bbaa9369107884bc"/>
			<DO name="Pos" type="Pos$oscd$_fb08409822e126d6"/>
			<DO name="Dsc" type="Dsc$oscd$_d915d66d9e42a575"/>
		</LNodeType>
		<LNodeType lnClass="CILO" desc="Interlocking" id="CILO$oscd$_aa7ec79ef27309b1">
			<DO name="Beh" type="Beh$oscd$_c6ed035c8137b35a"/>
			<DO name="EnaCls" type="EnaCls$oscd$_d915d66d9e42a575"/>
			<DO name="EnaOpn" type="EnaOpn$oscd$_d915d66d9e42a575"/>
		</LNodeType>
		<LNodeType lnClass="CSWI" desc="Contol" id="CSWI$oscd$_62a9bffb9574f30b">
			<DO name="Beh" type="Beh$oscd$_c6ed035c8137b35a"/>
			<DO name="Pos" type="Pos$oscd$_d6386b0989a52876"/>
			<DO name="OpOpn" type="OpOpn$oscd$_8598a343000a4c8c" transient="true"/>
			<DO name="OpCls" type="OpCls$oscd$_8598a343000a4c8c" transient="true"/>
		</LNodeType>
		<LNodeType lnClass="PTOC" desc="Definite Time" id="PTOC$oscd$_02fbb098f9687714">
			<DO name="Beh" type="Beh$oscd$_c6ed035c8137b35a"/>
			<DO name="Op" type="Op$oscd$_8598a343000a4c8c" transient="true"/>
			<DO name="Str" type="Str$oscd$_ba49448ecb46113a"/>
			<DO name="OpDlTmms" type="OpDlTmms$oscd$_f1c0d370e7430e0c"/>
			<DO name="StrVal" type="StrVal$oscd$_70f83e1c005744f2"/>
		</LNodeType>
		<DOType cdc="ASG" id="VRtg$oscd$_70f83e1c005744f2">
			<DA name="setMag" fc="SE" bType="Struct" type="setMag$oscd$_ed49c2f7a55ad05a"/>
		</DOType>
		<DOType cdc="SAV" id="VolSv$oscd$_ff92aedee5482392">
			<DA name="instMag" fc="MX" bType="Struct" type="instMag$oscd$_ed49c2f7a55ad05a"/>
			<DA name="q" fc="MX" qchg="true" bType="Quality"/>
			<DA name="sVC" fc="CF" dchg="true" bType="Struct" type="sVC$oscd$_df6488ea078bf55c"/>
		</DOType>
		<DOType cdc="ASG" id="ARtg$oscd$_70f83e1c005744f2">
			<DA name="setMag" fc="SE" bType="Struct" type="setMag$oscd$_ed49c2f7a55ad05a"/>
		</DOType>
		<DOType cdc="ASG" id="HzRtg$oscd$_70f83e1c005744f2">
			<DA name="setMag" fc="SE" bType="Struct" type="setMag$oscd$_ed49c2f7a55ad05a"/>
		</DOType>
		<DOType cdc="SAV" id="AmpSv$oscd$_ff92aedee5482392">
			<DA name="instMag" fc="MX" bType="Struct" type="instMag$oscd$_ed49c2f7a55ad05a"/>
			<DA name="q" fc="MX" qchg="true" bType="Quality"/>
			<DA name="sVC" fc="CF" dchg="true" bType="Struct" type="sVC$oscd$_df6488ea078bf55c"/>
		</DOType>
		<DOType cdc="ENS" id="SwTyp$oscd$_082f086f651ace0d">
			<DA name="stVal" fc="ST" dchg="true" dupd="true" bType="Enum" type="stVal$oscd$_e72af539034476a1"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="SPC" id="BlkCls$oscd$_637b3bee56ce66e4">
			<DA name="ctlModel" fc="CF" dchg="true" bType="Enum" type="ctlModel$oscd$_f80264355419aeff"/>
		</DOType>
		<DOType cdc="SPC" id="BlkOpn$oscd$_637b3bee56ce66e4">
			<DA name="ctlModel" fc="CF" dchg="true" bType="Enum" type="ctlModel$oscd$_f80264355419aeff"/>
		</DOType>
		<DOType cdc="SPS" id="Loc$oscd$_d915d66d9e42a575">
			<DA name="stVal" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="INS" id="OpCnt$oscd$_bbaa9369107884bc">
			<DA name="stVal" fc="ST" dchg="true" dupd="true" bType="INT32"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="DPC" id="Pos$oscd$_fb08409822e126d6">
			<DA name="stVal" fc="ST" dchg="true" bType="Dbpos"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
			<DA name="ctlModel" fc="CF" dchg="true" bType="Enum" type="ctlModel$oscd$_a77afbb487d8bdc0"/>
		</DOType>
		<DOType cdc="SPS" id="Dsc$oscd$_d915d66d9e42a575">
			<DA name="stVal" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="SPS" id="EnaCls$oscd$_d915d66d9e42a575">
			<DA name="stVal" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="SPS" id="EnaOpn$oscd$_d915d66d9e42a575">
			<DA name="stVal" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="DPC" id="Pos$oscd$_d6386b0989a52876">
			<DA name="stVal" fc="ST" dchg="true" bType="Dbpos"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
			<DA name="ctlModel" fc="CF" dchg="true" bType="Enum" type="ctlModel$oscd$_f80264355419aeff"/>
			<DA name="sboTimeout" fc="CF" dchg="true" bType="INT32U"/>
			<DA name="operTimeout" fc="CF" dchg="true" bType="INT32U"/>
			<DA name="SBOw" fc="CO" bType="Struct" type="SBOw$oscd$_0d1ece84b67fe837"/>
			<DA name="Oper" fc="CO" bType="Struct" type="Oper$oscd$_0d1ece84b67fe837"/>
			<DA name="Cancel" fc="CO" bType="Struct" type="Cancel$oscd$_23b00f788591fc22"/>
		</DOType>
		<DOType cdc="ACT" id="OpOpn$oscd$_8598a343000a4c8c">
			<DA name="general" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="ACT" id="OpCls$oscd$_8598a343000a4c8c">
			<DA name="general" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="ENS" id="Beh$oscd$_c6ed035c8137b35a">
			<DA name="stVal" fc="ST" dchg="true" dupd="true" bType="Enum" type="stVal$oscd$_48ba16345b8e7f5b"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="ACT" id="Op$oscd$_8598a343000a4c8c">
			<DA name="general" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="ACD" id="Str$oscd$_ba49448ecb46113a">
			<DA name="general" fc="ST" dchg="true" bType="BOOLEAN"/>
			<DA name="dirGeneral" fc="ST" dchg="true" bType="Enum" type="dirGeneral$oscd$_ba7b8abb9d154a3c"/>
			<DA name="q" fc="ST" qchg="true" bType="Quality"/>
			<DA name="t" fc="ST" bType="Timestamp"/>
		</DOType>
		<DOType cdc="ING" id="OpDlTmms$oscd$_f1c0d370e7430e0c">
			<DA name="setVal" fc="SE" bType="INT32"/>
		</DOType>
		<DOType cdc="ASG" id="StrVal$oscd$_70f83e1c005744f2">
			<DA name="setMag" fc="SE" bType="Struct" type="setMag$oscd$_ed49c2f7a55ad05a"/>
		</DOType>
		<DAType id="instMag$oscd$_ed49c2f7a55ad05a">
			<BDA name="f" bType="FLOAT32"/>
		</DAType>
		<DAType id="sVC$oscd$_df6488ea078bf55c">
			<BDA name="scaleFactor" bType="FLOAT32"/>
			<BDA name="offset" bType="FLOAT32"/>
		</DAType>
		<DAType id="origin$oscd$_cfc683368475eafc">
			<BDA name="orCat" bType="Enum" type="orCat$oscd$_711cd9acd4dad897"/>
			<BDA name="orIdent" bType="Octet64"/>
		</DAType>
		<DAType id="SBOw$oscd$_0d1ece84b67fe837">
			<BDA name="ctlVal" bType="BOOLEAN"/>
			<BDA name="origin" bType="Struct" type="origin$oscd$_cfc683368475eafc"/>
			<BDA name="ctlNum" bType="INT8U"/>
			<BDA name="T" bType="Timestamp"/>
			<BDA name="Test" bType="BOOLEAN"/>
			<BDA name="Check" bType="Check"/>
		</DAType>
		<DAType id="Oper$oscd$_0d1ece84b67fe837">
			<BDA name="ctlVal" bType="BOOLEAN"/>
			<BDA name="origin" bType="Struct" type="origin$oscd$_cfc683368475eafc"/>
			<BDA name="ctlNum" bType="INT8U"/>
			<BDA name="T" bType="Timestamp"/>
			<BDA name="Test" bType="BOOLEAN"/>
			<BDA name="Check" bType="Check"/>
		</DAType>
		<DAType id="Cancel$oscd$_23b00f788591fc22">
			<BDA name="ctlVal" bType="BOOLEAN"/>
			<BDA name="origin" bType="Struct" type="origin$oscd$_cfc683368475eafc"/>
			<BDA name="ctlNum" bType="INT8U"/>
			<BDA name="T" bType="Timestamp"/>
			<BDA name="Test" bType="BOOLEAN"/>
		</DAType>
		<DAType id="setMag$oscd$_ed49c2f7a55ad05a">
			<BDA name="f" bType="FLOAT32"/>
		</DAType>
		<EnumType id="stVal$oscd$_e72af539034476a1">
			<EnumVal ord="1">Load Break</EnumVal>
			<EnumVal ord="2">Disconnector</EnumVal>
			<EnumVal ord="3">Earthing Switch</EnumVal>
			<EnumVal ord="4">High Speed Earthing Switch</EnumVal>
		</EnumType>
		<EnumType id="ctlModel$oscd$_a77afbb487d8bdc0">
			<EnumVal ord="0">status-only</EnumVal>
		</EnumType>
		<EnumType id="ctlModel$oscd$_f80264355419aeff">
			<EnumVal ord="0">status-only</EnumVal>
			<EnumVal ord="1">direct-with-normal-security</EnumVal>
			<EnumVal ord="2">sbo-with-normal-security</EnumVal>
			<EnumVal ord="3">direct-with-enhanced-security</EnumVal>
			<EnumVal ord="4">sbo-with-enhanced-security</EnumVal>
		</EnumType>
		<EnumType id="orCat$oscd$_711cd9acd4dad897">
			<EnumVal ord="0">not-supported</EnumVal>
			<EnumVal ord="1">bay-control</EnumVal>
			<EnumVal ord="2">station-control</EnumVal>
			<EnumVal ord="3">remote-control</EnumVal>
			<EnumVal ord="4">automatic-bay</EnumVal>
			<EnumVal ord="5">automatic-station</EnumVal>
			<EnumVal ord="6">automatic-remote</EnumVal>
			<EnumVal ord="7">maintenance</EnumVal>
			<EnumVal ord="8">process</EnumVal>
		</EnumType>
		<EnumType id="stVal$oscd$_48ba16345b8e7f5b">
			<EnumVal ord="1">on</EnumVal>
			<EnumVal ord="2">blocked</EnumVal>
			<EnumVal ord="3">test</EnumVal>
			<EnumVal ord="4">test/blocked</EnumVal>
			<EnumVal ord="5">off</EnumVal>
		</EnumType>
		<EnumType id="dirGeneral$oscd$_ba7b8abb9d154a3c">
			<EnumVal ord="0">unknown</EnumVal>
			<EnumVal ord="1">forward</EnumVal>
			<EnumVal ord="2">backward</EnumVal>
			<EnumVal ord="3">both</EnumVal>
		</EnumType>
	</DataTypeTemplates>
</SCL>');
