/*
 * @(#)NewvarReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>NewvarReader</code> is a class to read the newvar.cat.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 * <p>
 * How to parse the record:
 * <p>
 * <h3>Variable Star Catalogues</h3>
 * <p>
 * <b>NovaStar</b><br>
 * <pre>
 *   Name := .*nova\d\d\d\d.*<br>
 *     Name [Data]
 * </pre>
 * <p>
 * <b>AgprsStar</b><br>
 * <pre>
 *   Name := AGPRS_J.*
 *     Name Rmag"R" "var"
 * </pre>
 * <p>
 * <b>AsasStar</b><br>
 * <pre>
 *   Name := ASAS_J.*
 *     Name Imag"I" "("Range")" [Data]
 * </pre>
 * <p>
 * <b>Asas3Star</b><br>
 * <pre>
 *   Name := ASAS3_J.*
 *     Name Vmag"V" "("Range")" Type "P="Period [Data]
 * </pre>
 * <p>
 * <b>BrhVStar</b><br>
 * <pre>
 *   Name := (BeV|BrhV)\d+
 *     Name Max Min+System Type [Period] [Epoch]
 * </pre>
 * <p>
 * <b>BnVStar</b><br>
 * <pre>
 *   Name := BnV\d+
 *     Name Max+System Min+System Type
 *     Name Type
 * </pre>
 * <p>
 * <b>ChsmStar</b><br>
 * <pre>
 *   Name := CHSM\d+
 *     Name "J="Jmag "H="Hmag "K="Kmag "Index="Index
 * </pre>
 * <p>
 * <b>CsakVStar</b><br>
 * <pre>
 *   Name := Csak_V.*
 *     Name Max Min+System Type [Spectrum] IBVS
 *     Name Mag+System Type IBVS
 * </pre>
 * <p>
 * <b>DenisBulgeStar</b><br>
 * <pre>
 *   Name := DENIS_Bulge.*
 *     Name [Mag+System] "("Range+System")"
 * </pre>
 * <p>
 * <b>DirectVStar</b><br>
 * <pre>
 *   Name := DIRECT_V.*
 *     Name Vmag"V" Type "P="Period
 *     Name Vmag"V" Type [Data]
 * </pre>
 * <p>
 * <b>Eros2GsaStar</b><br>
 * <pre>
 *   Name := EROS2\_GSA\_J.*
 *     Name Rmag"R" "("Rrange"R)" Vmag"V" "("Vrange"V)" Type ["P="Period]
 * </pre>
 * <p>
 * <p>
 * <b>ErosStar</b><br>
 * <pre>
 *   Name := EROS1.*
 *     Name Max Min+System Type
 *   Name := EROS2.*
 *     Name Mag+System Type "P="Period
 * </pre>
 * <b>Fastt2Star</b><br>
 * <pre>
 *   Name := FASTT2\-.*
 *     Name Rmag "("Range")R" [Data]
 * </pre>
 * <p>
 * <b>GrbStar</b><br>
 * <pre>
 *   Name := GRB.*[^var]
 *     Name Mag+System [Data]
 *     Name "GRB"
 * </pre>
 * <p>
 * <b>Fastt1Star</b><br>
 * <pre>
 *   Name := HS\d+
 *     Name Rmag "("Range")R" [Data]
 * </pre>
 * <p>
 * <b>HadVStar</b><br>
 * <pre>
 *   Name := HadV.*
 *     Name Max+System Min+System [Type [Period [Epoch]]]
 * </pre>
 * <p>
 * <b>HassfortherVStar</b><br>
 * <pre>
 *   Name := HassfortherV.*
 *     Name Max (Min+System | "-") Type "P="Period
 *     Name Max (Min+System | "-") [Type [Period]]
 *     Name Max "("Range")"System Type "P="Period
 *     Name Max "("Range")"System [Type [Period]]
 * </pre>
 * <p>
 * <b>HrmVStar</b><br>
 * <pre>
 *   Name := HrmV_J.*
 *     Name Max Min+System Type Period
 * </pre>
 * <p>
 * <b>IbvsStar</b><br>
 * <pre>
 *   Name := IBVS\d+_No.\d+
 *     Name Max Min+System Type Period Epoch
 *     Name Mag "("Range")"+System Type Period Epoch
 *     Name Mag+System - Type Period Epoch
 * </pre>
 * <p>
 * <b>IsogalPStar</b><br>
 * <pre>
 *   Name := ISOGALP_J.*
 *     Name Vmag"V" "("Range")" [Type] "logP="logP
 * </pre>
 * <p>
 * <b>IsvStar</b><br>
 * <pre>
 *   Name := ISV.*
 *     Name "var?"
 * </pre>
 * <p>
 * <b>LdStar</b><br>
 * <pre>
 *   Name := LD\d+
 *     Name Max Min[+System] Type [Period [Epoch]]
 * </pre>
 * <p>
 * <b>MoaStar</b><br>
 * <pre>
 *   Name := MOA\-.*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>MisVStar</b><br>
 * <pre>
 *   Name := MisV.*
 *     Name Max Min+System Type Period Epoch
 * </pre>
 * <p>
 * <b>NgStar</b><br>
 * <pre>
 *   Name := Ng_var.*
 *     Name Rmag"R" Type [Period]
 * </pre>
 * <p>
 * <b>OgleLtStar</b><br>
 * <pre>
 *   Name := OGLE-LT.*
 *     Name Imag"I" Type
 * </pre>
 * <p>
 * <b>OgleTrStar</b><br>
 * <pre>
 *   Name := OGLE-TR\-\d+
 *     Name Imag"I" "("Range")" Type Period Epoch ["V-I="V-I]
 * </pre>
 * <p>
 * <b>OgleEwsStar</b><br>
 * <pre>
 *   Name := OGLE\-\d\d\d\d.*
 *     Name Imag"I" "("Dmag")" Tmax "tau="tau "Amax="Amax
 * </pre>
 * <p>
 * <b>OglePeriodicStar</b><br>
 * <pre>
 *   Name := OGLE\-.*
 *     Name Imag"I" "("Range")" Type Period Epoch "V-I="V-I
 * </pre>
 * <p>
 * <b>PejStar</b><br>
 * <pre>
 *   Name := Pej\d+
 *     Name Max Min+System Type
 *     Name Mag+System "("Range")" Type
 * </pre>
 * <p>
 * <b>TaQStar</b><br>
 * <pre>
 *   Name := Q\d\d\d\d\/\d+
 *     Name Max Min [Type [Period]]
 *     Name [Data]
 * </pre>
 * <p>
 * <b>Rotse1Star</b><br>
 * <pre>
 *   Name := ROTSE1_J.*
 *     Name Mag "("Range")" [Data]
 * </pre>
 * <p>
 * <b>RosatStar</b><br>
 * <pre>
 *   Name := RXJ.*
 *     Name Max Min Type
 *     Name Mag Type
 * </pre>
 * <p>
 * <b>SavsStar</b><br>
 * <pre>
 *   Name := SAVS.*
 *     Name Mag "("Range")"+System Type Period Epoch Data
 * </pre>
 * <p>
 * <b>StareStar</b><br>
 * <pre>
 *   Name := STARE_.*
 *     Name [Mag] "("Range")" Type Period
 * </pre>
 * <p>
 * <b>TassStar</b><br>
 * <pre>
 *   Name := TASS_J.*
 *     Name Max Min+System [Type [Data]]
 *     Name "var"
 *   Name := TASS_Var.*
 *     Name Max Min+System Type Period
 *     Name Mag "("Range")" Type Period
 *   Name := .*
 *     Name Max Min+System Type [Period [Epoch]] "TASS(".*")"
 *     Name Mag "("Range")"+System Type [Period [Epoch]] "TASS(".*")"
 *     Name Mag+System "("Range")" Type [Period [Epoch]] "TASS(".*")"
 * </pre>
 * <p>
 * <b>TaVStar</b><br>
 * <pre>
 *   Name := TAV.*
 *     Name Max Min [Type [Period]]
 *     Name [Data]
 * </pre>
 * <p>
 * <b>TaSVStar</b><br>
 * <pre>
 *   Name := TASV.*
 *     Name Max Min [Type [Period]]
 *     Name [Data]
 * </pre>
 * <p>
 * <b>TerzVStar</b><br>
 * <pre>
 *   Name := TerzV.*
 *     Name Max Min+System
 * </pre>
 * <p>
 * <b>TmzVStar</b><br>
 * <pre>
 *   Name := TmzV.*
 *     Name Max Min+System Type [Period [Epoch]]
 * </pre>
 * <p>
 * <b>ToaVStar</b><br>
 * <pre>
 *   Name := ToaV.*
 *     Name Max Min+System Type
 * </pre>
 * <p>
 * <b>TbrVStar</b><br>
 * <pre>
 *   Name := TbrV.*
 *     Name Max Min+System Type Period
 * </pre>
 * <p>
 * <b>WakudaStar</b><br>
 * <pre>
 *   Name := Wakuda_new.*
 *     Name Max Min Type
 * </pre>
 * <p>
 * <b>YaloStar</b><br>
 * <pre>
 *   Name := YALO_J.*
 *     Name Max Min+System Type Period Epoch Data
 * </pre>
 * <p>
 * <b>Aaa97bStar</b><br>
 * <pre>
 *   Name := [AAA97b]J.*
 *     Name Max Min+System Type
 * </pre>
 * <p>
 * <b>Cks91Star</b><br>
 * <pre>
 *   Name := [CKS91].*
 *     Name Imag"I" "var" [Type]
 *     Name Imag "("Range")I" "var" [Type]
 *     Name "var" [Type]
 * </pre>
 * <p>
 * <b>D75Star</b><br>
 * <pre>
 *   Name := [D75].*
 *     Name Spectrum
 * </pre>
 * <p>
 * <b>Dhm99Star</b><br>
 * <pre>
 *   Name := [DHM99].*
 *     Name Vmag "("Range")V" [Data]
 * </pre>
 * <p>
 * <b>Gmc2001Star</b><br>
 * <pre>
 *   Name := [GMC2001].*
 *     Name Kmag"K" "("Range")" "P="Period
 * </pre>
 * <p>
 * <b>Ogle2BulgeStar</b><br>
 * <pre>
 *   Name := OGLE2\-BUL\-SC\d+\-V\d+
 *     Name Imag"I" "("Range")" Type
 * </pre>
 * <p>
 * <b>SdssCvStar</b><br>
 * <pre>
 *   Name := SDSSp_J.*
 *     Name Type Mag
 * </pre>
 * <p>
 * <b>SdssQuasarStar</b><br>
 * <pre>
 *   Name := SDSS_J.*
 *     Name "g="Gmag "z="z
 * </pre>
 * <p>
 * <b>TychoVarStar</b><br>
 * <pre>
 *   Name := TYC\d+\.\d+
 *     Name Status Vmag"V" "("Range")" "P="Period
 * </pre>
 * <p>
 * <b>AfaStar</b><br>
 * <pre>
 *   Name := AFASV.*
 *     Name Max Min+System
 * </pre>
 * <p>
 * <b>Ngc6712VStar</b><br>
 * <pre>
 *   Name := NGC6712_V\d+
 *     Name
 * </pre>
 * <p>
 * <h3>Other Catalogues</h3>
 * <p>
 * <b>Qz2Star</b><br>
 * <pre>
 *   Name := 2QZ_J.*
 *     Name Type "B="Bmag "z="z
 * </pre>
 * <p>
 * <b>BisStar</b><br>
 * <pre>
 *   Name := BIS\d+
 *     Name [Mag [Spectrum]]
 * </pre>
 * <p>
 * <b>BpsStar</b><br>
 * <pre>
 *   Name := BPS_.*
 *     Name [Data]
 *   Name := BS\d\d\d\d\d.\d\d\d\d
 *     Name "V="Vmag "B-V="B-V [Data]
 *   Name := CS\d\d\d\d\d.\d\d\d\d
 *     Name "V="Vmag "B-V="B-V [Data]
 * </pre>
 * <p>
 * <b>CeStar</b><br>
 * <pre>
 *   Name := CE\-.*
 *     Name Rmag"R" "mu="mu
 * </pre>
 * <p>
 * <b>CghaStar</b><br>
 * <pre>
 *   Name := CGHA\d+
 *     Name Mag [Data]
 * </pre>
 * <p>
 * <b>ClsStar</b><br>
 * <pre>
 *   Name := CLS\d.*
 *     Name Rmag"R" "Sp="Spectrum [Data]
 * </pre>
 * <p>
 * <b>CaseAFStar</b><br>
 * <pre>
 *   Name := CaseA\-F.*
 *     Name Bmag"B" [Data]
 * </pre>
 * <p>
 * <b>EdinburghStar</b><br>
 * <pre>
 *   Name := Edinburgh\d\-.*
 *     Name Imag"I" Spectrum
 * </pre>
 * <p>
 * <b>EcStar</b><br>
 * <pre>
 *   Name := EC\d\d\d\d\d.\d\d\d\d
 *     Name "V="Vmag "B-V="B-V [Data]
 * </pre>
 * <p>
 * <b>Zhm99Star</b><br>
 * <pre>
 *   Name := EIS_.*
 *     Name "I="Imag
 * </pre>
 * <p>
 * <b>EsoHaStar</b><br>
 * <pre>
 *   Name := ESO-HA.*
 *     Name Mag [Data]
 * </pre>
 * <p>
 * <b>EuveStar</b><br>
 * <pre>
 *   Name := EUVE_J.*
 *     Name Mag ID Spectrum
 * </pre>
 * <p>
 * <b>FbqsStar</b><br>
 * <pre>
 *   Name := FBQS_J.*
 *     Name Type "r="rmag ["z="z]
 * </pre>
 * <p>
 * <b>FbsStar</b><br>
 * <pre>
 *   Name := FBS.*
 *     Name pgmag"pg" [Data]
 *   Name := FBS.*
 *     Name rmag"r" Spectrum
 * </pre>
 * <p>
 * <b>FocapStar</b><br>
 * <pre>
 *   Name := FOCAP_.*
 *     Name Type Bmag"B" "U-B="U-B ["z="z]
 * </pre>
 * <p>
 * <b>LowellGStar</b><br>
 * <pre>
 *   Name := G\d+\-\d+(\S)?
 *     Name pgmag"pg" "mu="mu
 * </pre>
 * <p>
 * <b>HaroChaviraStar</b><br>
 * <pre>
 *   Name := HaroChavira\d*
 *     Name Imag"I" Spectrum
 * </pre>
 * <p>
 * <b>HbcStar</b><br>
 * <pre>
 *   Name := HBC.*
 *     Name "PMS" "V="Vmag Spectrum [Data]
 * </pre>
 * <p>
 * <b>HbhaStar</b><br>
 * <pre>
 *   Name := HBH.*
 *     Name "em" "V="Vmag Spectrum
 * </pre>
 * <p>
 * <b>HeStar</b><br>
 * <pre>
 *   Name := HE\d\d\d\d.\d\d\d\d
 *     Name Bmag"B" "z="z
 *     Name Bmag"B" Type [Data]
 * </pre>
 * <p>
 * <b>HhStar</b><br>
 * <pre>
 *   Name := HH\d+
 *     Name [Data]
 * </pre>
 * <p>
 * <b>HmxbStar</b><br>
 * <pre>
 *   Name := HMXB_
 *     Name "HMXB("Type")" "V="Vmag [Data]
 * </pre>
 * <p>
 * <b>HsStar</b><br>
 * <pre>
 *   Name := HS\d\d\d\d.\d\d\d\d
 *     Name Bmag"B" Type ["z="z]
 * </pre>
 * <p>
 * <b>IfmStar</b><br>
 * <pre>
 *   Name := IFM\-.*
 *     Name "V="Vmag "B-V="B-V
 * </pre>
 * <p>
 * <b>JlStar</b><br>
 * <pre>
 *   Name := JL\d+
 *     Name Mag
 * </pre>
 * <p>
 * <b>KuvStar</b><br>
 * <pre>
 *   Name := KUV.*
 *     Name Mag U-B
 * </pre>
 * <p>
 * <b>KisoAStar</b><br>
 * <pre>
 *   Name := Kiso_A\-.*
 *     Name "em" "V="Vmag
 *     Name "em" "-"
 * </pre>
 * <p>
 * <b>KisoCStar</b><br>
 * <pre>
 *   Name := Kiso_C\d\-.*
 *     Name Vmag"V"
 * </pre>
 * <p>
 * <b>LbqsStar</b><br>
 * <pre>
 *   Name := LBQS.*
 *     Name Bmag"B" "z="z
 *   Name := LBQS_B.*
 *     Name Bmag"B" "z="z
 * </pre>
 * <p>
 * <b>Lf1Star</b><br>
 * <pre>
 *   Name := LF1[ABC]\d+
 *     Name [Imag"I"] Spectrum
 * </pre>
 * <p>
 * <b>LhsStar</b><br>
 * <pre>
 *   Name := LHS.*
 *     Name rmag"r" Spectrum "mu="mu
 * </pre>
 * <p>
 * <b>LmcStar</b><br>
 * <pre>
 *   Name := LMC_.*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>LmxbStar</b><br>
 * <pre>
 *   Name := LMXB_
 *     Name "LMXB("Type")" "V="Vmag [Data]
 * </pre>
 * <p>
 * <b>LsStar</b><br>
 * <pre>
 *   Name := LS(I|II|III|IV|V|VI)(\+|\-).*
 *     Name pmag"p" [Type]
 * </pre>
 * <p>
 * <b>LssStar</b><br>
 * <pre>
 *   Name := LSS.*
 *     Name pmag"p" Type
 * </pre>
 * <p>
 * <b>LwdStar</b><br>
 * <pre>
 *   Name := LWD.*
 *     Name pgmag"pg" Type
 * </pre>
 * <p>
 * <b>LanningStar</b><br>
 * <pre>
 *   Name := Lanning.*
 *     Name ["B="Bmag] ["U-B="U-B] [Data]
 * </pre>
 * <p>
 * <b>MlaStar</b><br>
 * <pre>
 *   Name := MLA.*
 *     Name "em" [Data]
 * </pre>
 * <p>
 * <b>KwbbeStar</b><br>
 * <pre>
 *   Name := NGC(1818|1948|2004|2100|330|346):KWBBe.*
 *     Name "em" "V="Vmag [Data]
 * </pre>
 * <p>
 * <b>OmhrStar</b><br>
 * <pre>
 *   Name := OMHR_J.*
 *     Name "U="Umag "V="Vmag [Data]
 * </pre>
 * <p>
 * <b>PbStar</b><br>
 * <pre>
 *   Name := PB\d+
 *     Name Bmag"B" [Data]
 * </pre>
 * <p>
 * <b>PhlStar</b><br>
 * <pre>
 *   Name := PHL.*
 *     Name Pmag"p"
 * </pre>
 * <p>
 * <b>RjhaStar</b><br>
 * <pre>
 *   Name := RJHA.*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>SandStar</b><br>
 * <pre>
 *   Name := Sand.*
 *     Name ["V="Vmag] ["B-V="B-V] ["pmA="mu(R.A.)] ["pmD="mu(Decl.)]
 * </pre>
 * <p>
 * <b>SbsStar</b><br>
 * <pre>
 *   Name := SBS.*
 *     Name Mag (gal|stellar) [Data]
 * </pre>
 * <p>
 * <b>Mbh96Star</b><br>
 * <pre>
 *   Name := UIT.*
 *     Name "V="Vmag "U-B="U-B [Data]
 * </pre>
 * <p>
 * <b>UmStar</b><br>
 * <pre>
 *   Name := UM\d.*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>UsStar</b><br>
 * <pre>
 *   Name := US\d.*
 *     Name Bmag"B" "col="col [Data]
 * </pre>
 * <p>
 * <b>VdbhStar</b><br>
 * <pre>
 *   Name := VDBH.*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>WdStar</b><br>
 * <pre>
 *   Name := WD\d\d\d\d\.\d\d\d.*
 *     Name [Mag] [Data]
 * </pre>
 * <p>
 * <b>WgStar</b><br>
 * <pre>
 *   Name := Wg\d+
 *     Name Vmag"V" [Data]
 * </pre>
 * <p>
 * <b>A64Star</b><br>
 * <pre>
 *   Name := [A64].*
 *     Name [bmag"b"] Spectrum
 * </pre>
 * <p>
 * <b>A72cStar</b><br>
 * <pre>
 *   Name := [A72c].*
 *     Name Spectrum [Data]
 * </pre>
 * <p>
 * <b>B86Star</b><br>
 * <pre>
 *   Name := [B86].*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>Bbe90Star</b><br>
 * <pre>
 *   Name := [BBE90].*
 *     Name Jmag"J" Type [Data]
 * </pre>
 * <p>
 * <b>Bfa97Star</b><br>
 * <pre>
 *   Name := [BFA97].*
 *     Name FUVmag"FUV" [Data]
 * </pre>
 * <p>
 * <b>Cbb98Star</b><br>
 * <pre>
 *   Name := [CBB98].*
 *     Name Vmag"V" [Data]
 * </pre>
 * <p>
 * <b>Ctt83Star</b><br>
 * <pre>
 *   Name := [CTT83].*
 *     Name [rmag"r"]
 * </pre>
 * <p>
 * <b>Di91Star</b><br>
 * <pre>
 *   Name := [DI91].*
 *     Name "V="Vmag "B-V="B-V
 * </pre>
 * <p>
 * <b>Hpj88Star</b><br>
 * <pre>
 *   Name := [HPJ88].*
 *     Name "V="Vmag "B-V="B-V
 * </pre>
 * <p>
 * <b>Kp2001Star</b><br>
 * <pre>
 *   Name := [KP2001].*
 *     Name Bmag"B" Spectrum
 * </pre>
 * <p>
 * <b>Ma93Star</b><br>
 * <pre>
 *   Name := [MA93].*
 *     Name "em"
 * </pre>
 * <p>
 * <b>Mh95Star</b><br>
 * <pre>
 *   Name := [MH95].*
 *     Name "C-star"
 * </pre>
 * <p>
 * <b>Mt91Star</b><br>
 * <pre>
 *   Name := [MT91].*
 *     Name "V="Vmag "B-V="B-V [Data]
 * </pre>
 * <p>
 * <b>Ow94Star</b><br>
 * <pre>
 *   Name := [OW94].*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>Rrw93Star</b><br>
 * <pre>
 *   Name := [RRW93].*
 *     Name "V="Vmag Spectrum
 * </pre>
 * <p>
 * <b>Mfl2000Star</b><br>
 * <pre>
 *   Name := [MFL2000].*
 *     Name
 * </pre>
 * <p>
 * <b>W59Star</b><br>
 * <pre>
 *   Name := [W59].*
 *     Name Imag"I" Spectrum
 * </pre>
 * <p>
 * <b>CfrsStar</b><br>
 * <pre>
 *   Name := CFRS.*
 *     Name "I="Imag "V-I="V-I "z="z
 * </pre>
 * <p>
 * <b>Cgcs3Star</b><br>
 * <pre>
 *   Name := CGCS_J.*
 *     Name "("Alias")" Bmag Vmag IRmag [Data]
 * </pre>
 * <p>
 * <b>UhaStar</b><br>
 * <pre>
 *   Name := UHA\d+
 *     Name Vmag"V" Bmag"B"
 * </pre>
 * <p>
 * <b>DoStar</b><br>
 * <pre>
 *   Name := DO\d+
 *     Name [Vmag] Spectrum
 * </pre>
 * <p>
 * <h3>Nebulae and Clusters Catalogues</h3>
 * <p>
 * <b>KugStar</b><br>
 * <pre>
 *   Name := KUG.*
 *     Name "B="Bmag
 * </pre>
 * <p>
 * <b>MrkStar</b><br>
 * <pre>
 *   Name := Mrk.*
 *     Name Mag "z="z
 * </pre>
 * <p>
 * <b>PnGStar</b><br>
 * <pre>
 *   Name := PN_G.*
 *     Name [Data]
 * </pre>
 * <p>
 * <b>Bdf99Star</b><br>
 * <pre>
 *   Name := [BDF99].*
 *     Name "PN"
 * </pre>
 * <p>
 * <h3>X-ray Catalogues</h3>
 * <p>
 * <b>Axg1Star</b><br>
 * <pre>
 *   Name := 1AXG_J.*
 *     Name Value"ct/ks" "hard="Hard
 * </pre>
 * <p>
 * <b>Rxs1Star</b><br>
 * <pre>
 *   Name := 1RXSJ.*
 *     Name Mag Type [Data]
 *   Name := 1RXS_J.*
 *     Name "V="[Vmag] Type [Data]
 * </pre>
 * <p>
 * <b>RxStar</b><br>
 * <pre>
 *   Name := RXJ.*
 *     Name "V="Vmag "B-V="B-V [Data]
 *     Name [Data]
 * </pre>
 * <p>
 * <b>Sax1Star</b><br>
 * <pre>
 *   Name := 1SAX_J.*
 *     Name "Xray="Intensity Type(including spaces)
 * </pre>
 * <p>
 * <b>E2Star</b><br>
 * <pre>
 *   Name := 2E_IPC.*
 *     Name "ultrasoft-X" [Data]
 * </pre>
 * <p>
 * <b>Re2Star</b><br>
 * <pre>
 *   Name := 2RE_J.*
 *     Name S1"cts(s1)" S2"cts(s2)" [Data]
 * </pre>
 * <p>
 * <b>FaustStar</b><br>
 * <pre>
 *   Name := FAUST.*
 *     Name "FUV="Intensity [Mag] Spectrum
 * </pre>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 February 12
 */

public class NewvarReader extends FileReader {
	/**
	 * Constructs an empty <code>NewvarReader</code>.
	 */
	public NewvarReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>NewvarReader</code> with URL of the 
	 * catalog file.
	 * @param url the URL of the catalog file.
	 */
	public NewvarReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Taichi Kato's Catalog of Variable Stars, etc., for VSNET";
	}

	/**
	 * Gets the next token. When the token equals "-", it returns "".
	 * @param st the tokenizer.
	 * @return the next token.
	 */
	private static String nextToken ( StringTokenizer st ) {
		String s = st.nextToken();
		if (s.equals("-"))
			s = "";
		return s;
	}

	/**
	 * Gets the magnitude or magnitude range.
	 * @param string the string indicating the magnitude or magnitude
	 * range.
	 * @return the magnitude or magnitude range.
	 */
	private static String getMagnitude ( String string ) {
		if (string.length() == 0)
			return "";

		int p = -1;
		int start = 0;
		if (string.charAt(0) == '(') {
			start = 1;
			p = string.indexOf(')');
		}

		if (p < 0) {
			start = 0;

			p = -1;
			while (p + 1 < string.length()  &&  ! ('0' <= string.charAt(p + 1)  &&  string.charAt(p + 1) <= '9'))
				p++;

			// In the case of "-V" for example.
			if (p >= 0  &&  p + 1 == string.length()) {
				if (string.charAt(0) == '-')
					return "";
			}

			while (p + 1 < string.length()  &&  
				   (('0' <= string.charAt(p + 1)  &&  string.charAt(p + 1) <= '9')  ||  string.charAt(p + 1) == '.'))
				p++;
			while (p + 1 < string.length()  &&  
				   (string.charAt(p + 1) == ':'  ||  string.charAt(p + 1) == '?'  ||  string.charAt(p + 1) == '+'  ||  string.charAt(p + 1) == '-'))
				p++;

			p++;
		}

		if (p < 0)
			return "";

		return string.substring(start, p);
	}

	/**
	 * Gets the magnitude system.
	 * @param string the string indicating the magnitude or magnitude
	 * range.
	 * @return the magnitude system.
	 */
	private static String getMagnitudeSystem ( String string ) {
		if (string.length() == 0)
			return "";

		int p = -1;
		if (string.charAt(0) == '(') {
			p = string.indexOf(')');
		}

		if (p < 0) {
			p = -1;
			while (p + 1 < string.length()  &&  ! ('0' <= string.charAt(p + 1)  &&  string.charAt(p + 1) <= '9'))
				p++;

			// In the case of "-V" for example.
			if (p >= 0  &&  p + 1 == string.length()) {
				if (string.charAt(0) == '-')
					return string.substring(1);
			}

			while (p + 1 < string.length()  &&  
				   (('0' <= string.charAt(p + 1)  &&  string.charAt(p + 1) <= '9')  ||  string.charAt(p + 1) == '.'))
				p++;
			while (p + 1 < string.length()  &&  
				   (string.charAt(p + 1) == ':'  ||  string.charAt(p + 1) == '?'  ||  string.charAt(p + 1) == '+'  ||  string.charAt(p + 1) == '-'))
				p++;
		}

		if (p < 0)
			return "";

		return string.substring(p + 1);
	}

	/**
	 * Gets the value after the key name and "=".
	 * @param string the string indicating the key and value.
	 * @return the value after the key name and "=".
	 */
	private static String getValue ( String string ) {
		if (string.length() == 0)
			return "";

		int p = string.indexOf('=');
		if (p > 0)
			string = string.substring(p + 1);

		if (string.equals("-"))
			string = "";

		return string;
	}

	/**
	 * Gets the data.
	 * @param st the <code>StringTokenizer</code> object.
	 * @return the data.
	 */
	private static String getData ( StringTokenizer st ) {
		String data = "";
		while (st.hasMoreTokens()) {
			String s = nextToken(st);
			if (s.length() > 0) {
				if (data.length() > 0)
					data += " ";
				data += s;
			}
		}

		return data;
	}

	/**
	 * Creates a <code>CatalogStar</code> object from the specified
	 * one line record in the file. If some more records are required
	 * to create a star object, it returns null. This method must be
	 * overrided in the subclasses.
	 * @param record the one line record in the file.
	 * @return the star object.
	 */
	public CatalogStar createStar ( String record ) {
		try {
			CatalogStar star = null;

			if (record.length() >= 26  &&  record.substring(0, 4).equals("2000")) {
				String coor_string = record.substring(5, 14).trim() + " " + record.substring(15, 24).trim();

				StringTokenizer st = new StringTokenizer(record.substring(25));
				String name = st.nextToken();

				if (name.indexOf("nova") > 0) {
					int p = name.indexOf("nova");
					String s = name.substring(p + 4);
					if (s.length() >= 4  &&  
						'0' <= s.charAt(0)  &&  s.charAt(0) <= '9'  &&
						'0' <= s.charAt(1)  &&  s.charAt(1) <= '9'  &&
						'0' <= s.charAt(2)  &&  s.charAt(2) <= '9'  &&
						'0' <= s.charAt(3)  &&  s.charAt(3) <= '9') {
						String constellation = name.substring(0, p);
						if (ConstellationTable.isConstellationCode(constellation))
							constellation = ConstellationTable.getConstellationCode(ConstellationTable.getConstellationNumber(constellation));
						int year = Integer.parseInt(s.substring(0, 4));
						String suffix = "";
						if (s.length() >= 5) {
							if (s.charAt(4) == '-')
								suffix = s.substring(5);
							else
								suffix = s.substring(4);
						}

						String data = getData(st);

						star = new NovaStar(constellation, year, suffix, coor_string);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("AGPRS_J")) {
					String r_mag = getMagnitude(nextToken(st));

					star = new AgprsStar(name.substring(7), coor_string, r_mag);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("ASAS_J")) {
					String i_mag = getMagnitude(nextToken(st));
					String range = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new AsasStar(name.substring(6), coor_string, i_mag, range);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("ASAS3_J")) {
					String v_mag = getMagnitude(nextToken(st));
					String range = getMagnitude(nextToken(st));
					String type = nextToken(st);
					String period = getValue(nextToken(st));
					String epoch = "";
					String data = getData(st);

					star = new Asas3Star(name.substring(7), v_mag, range, type, period, epoch);

					if (data.length() > 0)
						((DefaultStar)star).setData(data);
				}

				if ((name.length() >= 4  &&  name.substring(0, 3).equals("BeV"))  ||
					(name.length() >= 5  &&  name.substring(0, 4).equals("BrhV"))) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);
					String period = "";
					String epoch = "";
					if (st.hasMoreTokens())
						period = nextToken(st);
					if (st.hasMoreTokens())
						epoch = nextToken(st);

					if (name.charAt(0) == '0')
						name = name.substring(1);

					star = new BrhVStar(name.substring(name.indexOf('V') + 1), coor_string, max, min, mag_system, type, period, epoch);
				}

				if (name.length() >= 4  &&  name.substring(0, 3).equals("BnV")) {
					String max = "";
					String min = "";
					String type = nextToken(st);
					if (st.hasMoreTokens()) {
						max = type;
						min = nextToken(st);
						type = nextToken(st);
					}
						
					star = new BnVStar(name.substring(name.indexOf('V') + 1), coor_string, max, min, type);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("CHSM")) {
					String Jmag = getValue(nextToken(st));
					String Hmag = getValue(nextToken(st));
					String Kmag = getValue(nextToken(st));
					String index = getValue(nextToken(st));
						
					star = new ChsmStar(name.substring(4), coor_string, Jmag, Hmag, Kmag, index);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("Csak_V")) {
					String value = nextToken(st);
					String type = nextToken(st);
					String ibvs = nextToken(st);

					if (st.hasMoreTokens()) {
						String max = value;
						String min = getMagnitude(type);
						String mag_system = getMagnitudeSystem(type);
						type = ibvs;
						ibvs = nextToken(st);
						String spectrum = "";

						if (st.hasMoreTokens()) {
							spectrum = ibvs;
							ibvs = nextToken(st);
						}

						star = new CsakVStar(name.substring(6), coor_string, "", max, min, mag_system, type, spectrum, ibvs);
					} else {
						String mag = getMagnitude(value);
						String mag_system = getMagnitudeSystem(value);
						star = new CsakVStar(name.substring(6), coor_string, mag, "", "", mag_system, type, "", ibvs);
					}
				}

				if (name.length() >= 11  &&  name.substring(0, 11).equals("DENIS_Bulge")) {
					String mag = "";
					String range = nextToken(st);
					if (st.hasMoreTokens()) {
						mag = range;
						range = nextToken(st);
					}
					range = getMagnitude(range);

					star = new DenisBulgeStar(name.substring(11), coor_string, mag, range);
				}

				if (name.length() >= 8  &&  name.substring(0, 8).equals("DIRECT_V")) {
					String v_mag = getMagnitude(nextToken(st));
					String type = nextToken(st);
					String period = "";
					String data = "";

					if (st.hasMoreTokens()) {
						period = nextToken(st);

						if (period.indexOf("P=") == 0) {
							period = getValue(period);
						} else {
							data = period;
							period = "";
							while (st.hasMoreTokens())
								data += " " + nextToken(st);
						}
					}

					String s = name.substring(7);
					int p = s.indexOf('_');

					star = new DirectVStar(s.substring(0, p), s.substring(p + 1), coor_string, v_mag, type, period);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 11  &&  name.substring(0, 11).equals("EROS2_GSA_J")) {
					String Rmag = getMagnitude(nextToken(st));
					String Rrange = getMagnitude(getMagnitude(nextToken(st)));
					String Vmag = getMagnitude(nextToken(st));
					String Vrange = getMagnitude(getMagnitude(nextToken(st)));
					String type = nextToken(st);
					String period = "";
					if (st.hasMoreTokens())
						period = getValue(nextToken(st));

					star = new Eros2GsaStar(name.substring(11), coor_string, Rmag, Rrange, Vmag, Vrange, type, period);
				} else if (name.length() >= 5  &&  name.substring(0, 4).equals("EROS")) {
					if (name.charAt(4) == '1') {
						String max = nextToken(st);
						String value = nextToken(st);
						String min = getMagnitude(value);
						String mag_system = getMagnitudeSystem(value);
						String type = nextToken(st);
						star = new ErosStar(name.substring(4), coor_string, "", max, min, mag_system, type, "");
					} else {
						String value = nextToken(st);
						String mag = getMagnitude(value);
						String mag_system = getMagnitudeSystem(value);
						String type = nextToken(st);
						String period = nextToken(st).substring(2);
						star = new ErosStar(name.substring(4), coor_string, mag, "", "", mag_system, type, period);
					}
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("FASTT2-")) {
					String r_mag = nextToken(st);
					String range = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new Fastt2Star(name.substring(7), coor_string, r_mag, range);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("GRB")  &&  name.indexOf("var") < 0) {
					String value = nextToken(st);
					String mag = "";
					String mag_system = "";
					String data = "";

					if (value.equals("GRB") == false) {
						mag = getMagnitude(value);
						mag_system = getMagnitudeSystem(value);
						data = getData(st);
					}

					star = new GrbStar(name.substring(3), coor_string, mag, mag_system);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("HS")) {
					boolean valid = true;
					for (int i = 2 ; i < name.length() ; i++) {
						if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
							valid = false;
					}

					if (valid) {
						String r_mag = nextToken(st);
						String range = getMagnitude(nextToken(st));
						String data = getData(st);

						star = new Fastt1Star(name.substring(2), coor_string, r_mag, range);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("HadV")) {
					String max = getMagnitude(nextToken(st));
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = "";
					String period = "";
					String epoch = "";
					if (st.hasMoreTokens())
						type = nextToken(st);
					if (st.hasMoreTokens())
						period = nextToken(st);
					if (st.hasMoreTokens())
						epoch = nextToken(st);

					star = new HadVStar(name.substring(4), coor_string, max, min, mag_system, type, period, epoch);
				}

				if (name.length() >= 12  &&  name.substring(0, 12).equals("HassfortherV")) {
					String max = nextToken(st);

					String value = "";
					if (max.indexOf('-') > 0) {
						value = max.substring(max.indexOf('-') + 1);
						max = max.substring(0, max.indexOf('-'));
					} else {
						value = nextToken(st);
					}
					String type = "";
					String period = "";
					if (st.hasMoreTokens())
						type = nextToken(st);
					if (st.hasMoreTokens())
						period = getValue(nextToken(st));

					String min = "";
					String range = "";
					String mag_system = "";
					if (value.length() > 0) {
						if (value.charAt(0) == '('  &&  value.indexOf(')') > 0) {
							range = getMagnitude(value);
							mag_system = getMagnitudeSystem(value);
						} else {
							min = getMagnitude(value);
							mag_system = getMagnitudeSystem(value);
						}
					} else {
						mag_system = getMagnitudeSystem(max);
						max = getMagnitude(max);
					}

					star = new HassfortherVStar(name.substring(12), coor_string, max, min, range, mag_system, type, period);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("HrmV_J")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);
					String period = nextToken(st);

					star = new HrmVStar(name.substring(6), coor_string, max, min, mag_system, type, period);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("IBVS")  &&  name.indexOf("_No.") > 0) {
					int p = name.indexOf("_No.");
					int ibvs_number = Integer.parseInt(name.substring(4, p));
					int sequential_number = Integer.parseInt(name.substring(p + 4));

					String value = nextToken(st);
					String max = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					value = nextToken(st);
					boolean range = false;
					if (value.length() >= 1  &&  value.charAt(0) == '(')
						range = true;
					String min = getMagnitude(value);
					if (mag_system.length() == 0)
						mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);
					String period = nextToken(st);
					String epoch = nextToken(st);

					star = new IbvsStar(ibvs_number, sequential_number, coor_string);
					if (range)
						((IbvsStar)star).setMagnitudeByRange(max, min);
					else
						((IbvsStar)star).setMagnitudeByMaxAndMin(max, min);
					((IbvsStar)star).setMagSystem(mag_system);
					((IbvsStar)star).setType(type);
					((IbvsStar)star).setPeriod(period);
					((IbvsStar)star).setEpoch(epoch);
				}

				if (name.length() >= 9  &&  name.substring(0, 9).equals("ISOGALP_J")) {
					String v_mag = getMagnitude(nextToken(st));
					String range = nextToken(st);
					String logP = nextToken(st);
					String type = "";
					if (st.hasMoreTokens()) {
						type = logP;
						logP = nextToken(st);
					}
					logP = getValue(logP);

					int p = range.indexOf(')');
					range = range.substring(1, p);
					if (range.equals("-"))
						range = "";

					star = new IsogalPStar(name.substring(9), coor_string, v_mag, range, type, logP);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("ISV")) {
					star = new IsvStar(name.substring(3), coor_string);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("LD")) {
					boolean valid = true;
					for (int i = 2 ; i < name.length() ; i++) {
						if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
							valid = false;
					}

					if (valid) {
						String max = nextToken(st);
						String value = nextToken(st);
						String min = getMagnitude(value);
						String mag_system = getMagnitudeSystem(value);
						String type = nextToken(st);
						String period = "";
						String epoch = "";
						if (st.hasMoreTokens())
							period = nextToken(st);
						if (st.hasMoreTokens())
							epoch = nextToken(st);
						star = new LdStar(name.substring(2), coor_string, max, min, mag_system, type, period, epoch);
					}
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("MOA-")) {
					String data = getData(st);

					star = new MoaStar(name.substring(4), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("MisV")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);
					String period = nextToken(st);
					String epoch = nextToken(st);

					int number = Integer.parseInt(name.substring(4));
					Coor coor = Coor.create(coor_string);

					star = new MisVStar(number, coor);

					((MisVStar)star).setMagnitudeByMaxAndMin(max, min);
					((MisVStar)star).setMagSystem(mag_system);

					if (type.length() > 0)
						((MisVStar)star).setType(type);
					if (period.length() > 0)
						((MisVStar)star).setPeriod(period);
					if (epoch.length() > 0)
						((MisVStar)star).setEpoch(epoch);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("Ng_var")) {
					String r_mag = getMagnitude(nextToken(st));
					String type = nextToken(st);
					String period = "";
					if (st.hasMoreTokens())
						period = nextToken(st);

					star = new NgStar(name.substring(6), coor_string, r_mag, type, period);
				}

				if (name.length() >= 8  &&  name.substring(0, 8).equals("OGLE-LTV")) {
					String i_mag = getMagnitude(nextToken(st));
					String type = nextToken(st);

					star = new OgleLtStar(name.substring(8), coor_string, i_mag, type);
				}

				if (name.length() >= 8  &&  name.substring(0, 8).equals("OGLE-TR-")) {
					String i_mag = getMagnitude(nextToken(st));
					String range = getMagnitude(nextToken(st));
					String type = nextToken(st);
					String period = nextToken(st);
					String epoch = nextToken(st);
					String v_i = "";
					if (st.hasMoreTokens())
						v_i = getValue(nextToken(st));

					star = new OgleTrStar(name.substring(8), coor_string, i_mag, range, type, period, epoch, v_i);
				}

				if (name.length() >= 9  &&  name.substring(0, 5).equals("OGLE-")  &&
					'0' <= name.charAt(5)  &&  name.charAt(5) <= '9'  &&
					'0' <= name.charAt(6)  &&  name.charAt(6) <= '9'  &&
					'0' <= name.charAt(7)  &&  name.charAt(7) <= '9'  &&
					'0' <= name.charAt(8)  &&  name.charAt(8) <= '9') {
					String i_mag = getMagnitude(nextToken(st));
					String Dmag = getMagnitude(nextToken(st));
					String Tmax = nextToken(st);
					String tau = getValue(nextToken(st));
					String Amax = getValue(nextToken(st));

					star = new OgleEwsStar(name.substring(5), coor_string, i_mag, Dmag, Tmax, tau, Amax);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("OGLE-")  &&  star == null) {
					int p = name.lastIndexOf('-');
					String area = name.substring(5, p);
					String number = name.substring(p + 2);
					String i_mag = getMagnitude(nextToken(st));
					String range = getMagnitude(nextToken(st));
					String type = nextToken(st);
					String period = nextToken(st);
					String epoch = nextToken(st);
					String v_i = "";
					if (st.hasMoreTokens())
						v_i = getValue(nextToken(st));

					star = new OglePeriodicStar(area, number, coor_string, i_mag, range, type, period, epoch, v_i);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("Pej")) {
					String max = "";
					String min = "";
					String mag = "";
					String range = "";
					String mag_system = "";

					String value1 = nextToken(st);
					String value2 = nextToken(st);
					if (value2.charAt(0) == '(') {
						mag = getMagnitude(value1);
						range = getMagnitude(value2);
						mag_system = getMagnitudeSystem(value1);
					} else {
						max = getMagnitude(value1);
						min = getMagnitude(value2);
						mag_system = getMagnitudeSystem(value2);
					}

					String type = nextToken(st);

					star = new PejStar(name.substring(3), coor_string, max, min, mag, range, mag_system, type);
				}

				if (name.length() >= 2  &&  name.charAt(0) == 'Q') {
					int p = name.indexOf('/');
					if (p > 0) {
						boolean valid = true;
						for (int i = 1 ; i < name.length() ; i++) {
							if (i != p) {
								if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
									valid = false;
							}
						}

						if (valid) {
							String max = nextToken(st);

							valid = true;
							if (max.length() == 0)
								valid = false;
							for (int i = 0 ; i < max.length() ; i++) {
								if (! (('0' <= max.charAt(i)  &&  max.charAt(i) <= '9')  ||  max.charAt(i) == '.'))
									valid = false;
							}

							if (valid) {
								String min = nextToken(st);
								String type = "";
								String period = "";
								if (st.hasMoreTokens())
									type = nextToken(st);
								if (st.hasMoreTokens())
									period = nextToken(st);

								star = new TaQStar(name, coor_string, max, min, type, period);
							} else {
								String data = max;
								while (st.hasMoreTokens())
									data += " " + nextToken(st);

								star = new TaQStar(name, coor_string, "", "", "", "");
								((DefaultStar)star).setData(data);
							}
						}
					}
				}

				if (name.length() >= 8  &&  name.substring(0, 8).equals("ROTSE1_J")) {
					String mag = nextToken(st);
					String range = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new Rotse1Star(name.substring(8), coor_string, mag, range);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("SAVS")) {
					String mag = nextToken(st);
					String value = nextToken(st);
					String range = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);
					String period = nextToken(st);
					String epoch = nextToken(st);
					String data = getData(st);

					star = new SavsStar(name.substring(4), coor_string, mag, range, mag_system, type, period, epoch);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("STARE_")) {
					String range = nextToken(st);
					String type = nextToken(st);
					String period = nextToken(st);
					String mag = "";
					if (st.hasMoreTokens()) {
						mag = range;
						range = type;
						type = period;
						period = nextToken(st);
					}
					range = getMagnitude(range);

					star = new StareStar(name.substring(6), coor_string, mag, range, type, period);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("TASS_J")) {
					String max = nextToken(st);
					String min = "";
					String mag_system = "";
					String type = "";
					String data = "";

					if (st.hasMoreTokens()) {
						String value = nextToken(st);
						min = getMagnitude(value);
						mag_system = getMagnitudeSystem(value);
						if (st.hasMoreTokens())
							type = nextToken(st);
						data = getData(st);
					} else {
						max = "";
					}

					star = new TassStar("TASS " + name.substring(5), coor_string, "", "", max, min, mag_system, type, "", "");
					((DefaultStar)star).setData(data);
				}
				if (name.length() >= 8  &&  name.substring(0, 8).equals("TASS_Var")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String type = nextToken(st);
					String period = nextToken(st);

					if (value.length() > 0  &&  value.charAt(0) == '(') {
						String mag = max;
						String range = getMagnitude(value);
						star = new TassStar("TASS " + name.substring(5), coor_string, mag, range, "", "", "", type, period, "");
					} else {
						String min = getMagnitude(value);
						String mag_system = getMagnitudeSystem(value);
						star = new TassStar(name.substring(5), coor_string, "", "", max, min, mag_system, type, period, "");
					}
				}
				if (record.indexOf("TASS(") > 0) {
					if (name.indexOf("GSC") == 0)
						name = "GSC " + name.substring(3);
					else if (name.indexOf("BD") == 0)
						name = "BD " + name.substring(2);
					else if (name.indexOf("HD") == 0)
						name = "HD " + name.substring(2);
					else if (name.indexOf("IRAS") == 0)
						name = "IRAS " + name.substring(4);

					String mag1 = nextToken(st);
					String mag2 = nextToken(st);
					String type = "";
					String period = "";
					String epoch = "";
					String data = nextToken(st);
					if (data.indexOf("TASS(") != 0) {
						type = data;
						data = nextToken(st);
						if (data.indexOf("TASS(") != 0) {
							period = data;
							data = nextToken(st);
							if (data.indexOf("TASS(") != 0) {
								epoch = data;
								data = nextToken(st);
							}
						}
					}

					if (mag2.charAt(0) == '(') {
						String mag = getMagnitude(mag1);
						String range = getMagnitude(mag2);
						String mag_system = getMagnitudeSystem(mag1);
						if (mag_system.length() == 0)
							mag_system = getMagnitudeSystem(mag2);
						star = new TassStar(name, coor_string, mag, range, "", "", mag_system, type, period, epoch);
					} else {
						String max = getMagnitude(mag1);
						String min = getMagnitude(mag2);
						String mag_system = getMagnitudeSystem(mag2);
						star = new TassStar(name, coor_string, "", "", max, min, mag_system, type, period, epoch);
					}
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("TAV")) {
					String max = nextToken(st);

					boolean valid = true;
					if (max.length() == 0)
						valid = false;
					for (int i = 0 ; i < max.length() ; i++) {
						if (! (('0' <= max.charAt(i)  &&  max.charAt(i) <= '9')  ||  max.charAt(i) == '.'))
							valid = false;
					}

					if (valid) {
						String min = nextToken(st);
						String type = "";
						String period = "";
						if (st.hasMoreTokens())
							type = nextToken(st);
						if (st.hasMoreTokens())
							period = nextToken(st);

						star = new TaVStar(name.substring(3), coor_string, max, min, type, period);
					} else {
						String data = max;
						while (st.hasMoreTokens())
							data += " " + nextToken(st);

						star = new TaVStar(name.substring(3), coor_string, "", "", "", "");
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("TASV")) {
					String max = nextToken(st);

					boolean valid = true;
					if (max.length() == 0)
						valid = false;
					for (int i = 0 ; i < max.length() ; i++) {
						if (! (('0' <= max.charAt(i)  &&  max.charAt(i) <= '9')  ||  max.charAt(i) == '.'))
							valid = false;
					}

					if (valid) {
						String min = nextToken(st);
						String type = "";
						String period = "";
						if (st.hasMoreTokens())
							type = nextToken(st);
						if (st.hasMoreTokens())
							period = nextToken(st);

						star = new TaSVStar(name.substring(4), coor_string, max, min, type, period);
					} else {
						String data = max;
						while (st.hasMoreTokens())
							data += " " + nextToken(st);

						star = new TaSVStar(name.substring(4), coor_string, "", "", "", "");
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("TerzV")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);

					star = new TerzVStar(name.substring(5), coor_string, max, min, mag_system);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("TmzV")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = "";
					String period = "";
					String epoch = "";
					if (st.hasMoreTokens())
						type = nextToken(st);
					if (st.hasMoreTokens())
						period = nextToken(st);
					if (st.hasMoreTokens())
						epoch = nextToken(st);

					star = new TmzVStar(name.substring(4), coor_string, max, min, mag_system, type, period, epoch);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("ToaV")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);

					star = new ToaVStar(name.substring(4), coor_string, max, min, mag_system, type);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("TbrV")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);
					String period = nextToken(st);
					String epoch = "";

					star = new TbrVStar(name.substring(4), coor_string, max, min, mag_system, type, period, epoch);
				}

				if (name.length() >= 10  &&  name.substring(0, 10).equals("Wakuda_new")) {
					String max = nextToken(st);
					String min = nextToken(st);
					String type = nextToken(st);

					star = new WakudaStar(name.substring(10), coor_string, max, min, type);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("YALO_J")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);
					String period = nextToken(st);
					String epoch = nextToken(st);
					String data = getData(st);

					star = new YaloStar(name.substring(6), coor_string, max, min, mag_system, type, period, epoch);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 9  &&  name.substring(0, 9).equals("[AAA97b]J")) {
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);
					String type = nextToken(st);

					star = new Aaa97bStar(name.substring(9), coor_string, max, min, mag_system, type);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[CKS91]")) {
					String value = nextToken(st);
					String i_mag = "";
					String range = "";
					String type = "";
					if (value.equals("var")) {
					} else {
						i_mag = getMagnitude(value);

						value = nextToken(st);
						if (value.equals("var")) {
						} else {
							range = getMagnitude(value);
							nextToken(st);	// "var"
						}
					}
					if (st.hasMoreTokens())
						type = nextToken(st);

					star = new Cks91Star(name.substring(7), coor_string, i_mag, range, type);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("[D75]")) {
					String spectrum = nextToken(st);

					star = new D75Star(name.substring(5), coor_string, spectrum);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[DHM99]")) {
					String v_mag = nextToken(st);
					String range = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new Dhm99Star(name.substring(7), coor_string, v_mag, range);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 9  &&  name.substring(0, 9).equals("[GMC2001]")) {
					String k_mag = getMagnitude(nextToken(st));
					String range = getMagnitude(nextToken(st));
					String period = getValue(nextToken(st));

					star = new Gmc2001Star(name.substring(9), coor_string, k_mag, range, period);
				}

				if (name.length() >= 12  &&  name.substring(0, 12).equals("OGLE2-BUL-SC")) {
					int p = name.lastIndexOf('-');
					String area = name.substring(10, p);
					String number = name.substring(p + 2);
					String i_mag = getMagnitude(nextToken(st));
					String range = getMagnitude(nextToken(st));
					String type = nextToken(st);

					star = new Ogle2BulgeStar(area, number, coor_string, i_mag, range, type);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("SDSSp_J")) {
					String type = nextToken(st);
					String mag = nextToken(st);

					star = new SdssCvStar(name.substring(7), coor_string, mag, type);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("SDSS_J")  &&  record.indexOf("g=") > 0) {
					String g_mag = getValue(nextToken(st));
					String z = getValue(nextToken(st));

					star = new SdssQuasarStar(name.substring(6), coor_string, g_mag, z);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("TYC")) {
					String status = nextToken(st);

					if (status.length() == 1  &&  
						'A' <= status.charAt(0)  &&  status.charAt(0) <= 'Z') {
						String v_mag = getMagnitude(nextToken(st));
						String range = getMagnitude(nextToken(st));
						String period = getValue(nextToken(st));

						int p = name.indexOf('.');
						short tyc1 = Short.parseShort(name.substring(3, p));
						short tyc2 = Short.parseShort(name.substring(p+1));
						short tyc3 = 1;

						star = new TychoVarStar(tyc1, tyc2, tyc3, coor_string, v_mag, range, period, status);
					} else {
						String data = status;
						String data2 = getData(st);
						if (data2.length() > 0)
							data = data + " " + data2;

						star = new OtherVariableStar(name.replace('_', ' '), name, coor_string);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("AFASV")) {
					int p = name.lastIndexOf('_');
					String area = name.substring(6, p);
					int number = Integer.parseInt(name.substring(p+1));
					String max = nextToken(st);
					String value = nextToken(st);
					String min = getMagnitude(value);
					String mag_system = getMagnitudeSystem(value);

					star = new AfaStar(area, number, coor_string, max, min, mag_system);
				}

				if (name.length() >= 9  &&  name.substring(0, 9).equals("NGC6712_V")) {
					star = new Ngc6712VStar(name.substring(9), coor_string);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("2QZ_J")) {
					String type = nextToken(st);
					String b_mag = getValue(nextToken(st));
					String z = getValue(nextToken(st));

					star = new Qz2Star(name.substring(5), coor_string, b_mag, type, z);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("BIS")) {
					String mag = "";
					String spectrum = "";
					if (st.hasMoreTokens())
						mag = nextToken(st);
					if (st.hasMoreTokens())
						spectrum = nextToken(st);

					star = new BisStar(name.substring(3), coor_string, mag, spectrum);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("BPS_")) {
					String data = getData(st);

					int p = name.lastIndexOf("_");
					String category = name.substring(4, p);
					String number = name.substring(p + 1);

					star = new BpsStar(category, number, coor_string, "", "");
					((DefaultStar)star).setData(data);
				}
				if (name.length() == 12  &&  
					(name.substring(0, 2).equals("BS")  ||  name.substring(0, 2).equals("CS"))) {
					boolean valid = true;
					for (int i = 2 ; i < 12 ; i++) {
						if (i == 7) {
							if (name.charAt(i) != '+'  &&  name.charAt(i) != '-')
								valid = false;
						} else {
							if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
								valid = false;
						}
					}

					if (valid) {
						String v_mag = getValue(nextToken(st));
						String b_v = getValue(nextToken(st));
						String data = getData(st);

						star = new BpsStar(name.substring(0, 2), name.substring(2), coor_string, v_mag, b_v);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("CE-")) {
					String r_mag = getMagnitude(nextToken(st));
					String mu = getValue(nextToken(st));

					star = new CeStar(name.substring(3), coor_string, r_mag, mu);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("CGHA")) {
					String mag = nextToken(st);
					String data = getData(st);

					star = new CghaStar(name.substring(4), coor_string, mag);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 4  &&  name.substring(0, 3).equals("CLS")  &&
					'0' <= name.charAt(3)  &&  name.charAt(3) <= '9') {
					String r_mag = getMagnitude(nextToken(st));
					String spectrum = getValue(nextToken(st));
					String data = getData(st);

					star = new ClsStar(name.substring(3), coor_string, r_mag, spectrum);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("CaseA-F")) {
					String b_mag = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new CaseAFStar(name.substring(7), coor_string, b_mag);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 11  &&  name.substring(0, 9).equals("Edinburgh")) {
					String revision = name.substring(9, 10);
					String number = name.substring(11);
					String i_mag = getMagnitude(nextToken(st));
					String spectrum = nextToken(st);

					star = new EdinburghStar(revision, number, coor_string, i_mag, spectrum);
				}

				if (name.length() == 12  &&  name.substring(0, 2).equals("EC")) {
					boolean valid = true;
					for (int i = 2 ; i < 12 ; i++) {
						if (i == 7) {
							if (name.charAt(i) != '+'  &&  name.charAt(i) != '-')
								valid = false;
						} else {
							if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
								valid = false;
						}
					}

					if (valid) {
						String value = nextToken(st);
						String v_mag = "";
						String b_v = "";
						String data = "";
						if (value.indexOf("V=") == 0) {
							v_mag = getValue(value);
							b_v = getValue(nextToken(st));
							data = getData(st);

							star = new EcStar(name.substring(2), coor_string, v_mag, b_v);
							((DefaultStar)star).setData(data);
						}
					}
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("EIS_")) {
					String i_mag = getValue(nextToken(st));

					String s = name.substring(4);
					int p = 999;
					for (char ch = '0' ; ch <= '9' ; ch++) {
						if (s.indexOf(ch) >= 0  &&  p > s.indexOf(ch))
							p = s.indexOf(ch);
					}

					star = new Zhm99Star(s.substring(0, p), s.substring(p), coor_string, i_mag);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("ESO-HA")) {
					String mag = nextToken(st);
					String data = getData(st);

					star = new EsoHaStar(name.substring(6), coor_string, mag);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("EUVE_J")) {
					String mag = nextToken(st);
					String id = nextToken(st);
					String spectrum = nextToken(st);

					star = new EuveStar(name.substring(6), coor_string, mag, spectrum, id);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("FBQS_J")) {
					String type = nextToken(st);
					String r_mag = getValue(nextToken(st));
					String z = "";
					if (st.hasMoreTokens())
						z = getValue(nextToken(st));

					star = new FbqsStar(name.substring(6), coor_string, r_mag, type, z);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("FBS")) {
					String mag = nextToken(st);
					String mag_system = getMagnitudeSystem(mag);
					mag = getMagnitude(mag);
					String spectrum = "";
					String data = "";

					if (mag_system.equals("pg"))
						getData(st);
					if (mag_system.equals("r"))
						spectrum = nextToken(st);

					star = new FbsStar(name.substring(3), coor_string, mag, mag_system, spectrum);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("FOCAP_")) {
					String type = nextToken(st);
					String b_mag = getMagnitude(nextToken(st));
					String u_b = getValue(nextToken(st));
					String z = "";
					if (st.hasMoreTokens())
						z = getValue(nextToken(st));

					star = new FocapStar(name.substring(6, 9), name.substring(9), coor_string, b_mag, u_b, z);
				}

				if (name.length() >= 2  &&  name.charAt(0) == 'G') {
					boolean valid = true;
					int hyphen_count = 0;
					int hyphen_index = 0;
					for (int i = 1 ; i < name.length() ; i++) {
						if ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9') {
						} else if (name.charAt(i) == '-') {
							hyphen_count++;
							hyphen_index = i;
							break;
						} else {
							valid = false;
						}
					}
					if (hyphen_count != 1)
						valid = false;

					if (valid) {
						String pg_mag = getMagnitude(nextToken(st));
						String mu = getValue(nextToken(st));

						star = new LowellGStar(name.substring(1, hyphen_index), name.substring(hyphen_index + 1), coor_string, pg_mag, mu);
					}
				}

				if (name.length() >= 11  &&  name.substring(0, 11).equals("HaroChavira")) {
					String i_mag = getMagnitude(nextToken(st));
					String spectrum = nextToken(st);

					star = new HaroChaviraStar(name.substring(11), coor_string, i_mag, spectrum);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("HBC")) {
					String type = nextToken(st);
					String v_mag = getValue(nextToken(st));
					String spectrum = nextToken(st);
					String data = getData(st);

					star = new HbcStar(name.substring(3), coor_string, v_mag, spectrum);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("HBH")) {
					String type = nextToken(st);
					String v_mag = getValue(nextToken(st));
					String spectrum = nextToken(st);

					int p = name.indexOf('-');
					if (p > 0)
						star = new HbhaStar(name.substring(3, p), name.substring(p + 1), coor_string, v_mag, spectrum);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("HE")) {
					boolean valid = true;
					int hyphen_count = 0;
					int hyphen_index = 0;
					for (int i = 2 ; i < name.length() ; i++) {
						if ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9') {
						} else if (name.charAt(i) == '-') {
							hyphen_count++;
							hyphen_index = i;
						} else if (name.charAt(i) == '+') {
							hyphen_count++;
							hyphen_index = i;
						} else {
							valid = false;
						}
					}
					if (hyphen_count != 1)
						valid = false;

					if (valid) {
						String b_mag = getMagnitude(nextToken(st));
						String z = nextToken(st);
						String type = "";
						String data = "";

						if (z.indexOf("z=") == 0) {
							z = getValue(z);
						} else {
							type = z;
							z = "";
							data = getData(st);
						}

						star = new HeStar(name.substring(2), coor_string, b_mag, type, z);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 2  &&  name.substring(0, 2).equals("HH")) {
					String data = getData(st);

					star = new HhStar(name.substring(2), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("HMXB_")) {
					String type = getMagnitude(nextToken(st));
					type = type.substring(5, type.length() - 1);
					String v_mag = getValue(nextToken(st));
					String data = getData(st);

					star = new HmxbStar(name.substring(5), coor_string, v_mag, type);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("HS")) {
					boolean valid = true;
					int hyphen_count = 0;
					int hyphen_index = 0;
					for (int i = 2 ; i < name.length() ; i++) {
						if ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9') {
						} else if (name.charAt(i) == '-') {
							hyphen_count++;
							hyphen_index = i;
						} else if (name.charAt(i) == '+') {
							hyphen_count++;
							hyphen_index = i;
						} else {
							valid = false;
						}
					}
					if (hyphen_count != 1)
						valid = false;

					if (valid) {
						String b_mag = getMagnitude(nextToken(st));
						String type = nextToken(st);
						String z = "";
						if (st.hasMoreTokens())
							z = nextToken(st);

						if (z.equals("CV") == false) {
							z = getValue(z);

							star = new HsStar(name.substring(2), coor_string, b_mag, type, z);
						}
					}
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("IFM-")) {
					String v_mag = getValue(nextToken(st));
					String b_v = getValue(nextToken(st));

					String s = name.substring(5);
					while (s.charAt(0) == '0')
						s = s.substring(1);

					star = new IfmStar(name.substring(4, 5), s, coor_string, v_mag, b_v);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("JL")) {
					boolean valid = true;
					for (int i = 2 ; i < name.length() ; i++) {
						if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
							valid = false;
					}

					if (valid) {
						String mag = nextToken(st);

						star = new JlStar(name.substring(2), coor_string, mag);
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("KUV")) {
					String mag = nextToken(st);
					String u_b = getValue(nextToken(st));

					star = new KuvStar(name.substring(3), coor_string, mag, u_b);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("Kiso_A-")) {
					String type = nextToken(st);
					String v_mag = nextToken(st);
					if (v_mag.indexOf("V=") == 0)
						v_mag = getValue(v_mag);

					star = new KisoAStar(name.substring(7).replace('_', ' '), coor_string, v_mag);
				}

				if (name.length() >= 8  &&  name.substring(0, 6).equals("Kiso_C")) {
					String revision = name.substring(6, 7);
					String number = name.substring(8);
					String v_mag = getMagnitude(nextToken(st));

					star = new KisoCStar(revision, number, coor_string, v_mag);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("LBQS_B")) {
					String b_mag = getMagnitude(nextToken(st));
					String z = getValue(nextToken(st));

					star = new LbqsStar(name.substring(6), coor_string, b_mag, z);
				}
				if (name.length() >= 4  &&  name.substring(0, 4).equals("LBQS")  &&  star == null) {
					String b_mag = getMagnitude(nextToken(st));
					String z = getValue(nextToken(st));

					star = new LbqsStar(name.substring(4), coor_string, b_mag, z);
				}

				if (name.length() >= 5  &&  name.substring(0, 3).equals("LF1")) {
					if (name.charAt(3) == 'A'  ||  name.charAt(3) == 'B'  ||  name.charAt(3) == 'C') {
						String table = name.substring(3, 4);
						String number = name.substring(4);
						String i_mag = nextToken(st);
						String spectrum = "";
						if (st.hasMoreTokens()) {
							i_mag = getMagnitude(i_mag);
							spectrum = nextToken(st);
						} else {
							spectrum = i_mag;
							i_mag = "";
						}

						star = new Lf1Star(table, number, coor_string, i_mag, spectrum);
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("LHS")) {
					String r_mag = getMagnitude(nextToken(st));
					String spectrum = nextToken(st);
					String mu = getValue(nextToken(st));

					star = new LhsStar(name.substring(3), coor_string, r_mag, spectrum, mu);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("LMC_")) {
					String data = getData(st);

					star = new LmcStar(name.substring(4), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("LMXB_")) {
					String type = nextToken(st);
					type = type.substring(5, type.length() - 1);
					String v_mag = getValue(nextToken(st));
					String data = getData(st);

					star = new LmxbStar(name.substring(5), coor_string, v_mag, type);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 4  &&  name.substring(0, 2).equals("LS")) {
					if (name.indexOf('+') > 0  ||  name.indexOf('-') > 0) {
						int p = name.indexOf('+');
						if (p < 0)
							p = name.indexOf('-');

						String revision = name.substring(2, p);
						if (revision.equals("I")  ||  revision.equals("II")  ||  revision.equals("III")  ||  revision.equals("IV")  ||  revision.equals("V")  ||  revision.equals("VI")) {
							String value = nextToken(st);
							String p_mag = getMagnitude(value);
							String mag_system = getMagnitudeSystem(value);

							if (mag_system.equals("p")) {
								String type = "";
								if (st.hasMoreTokens())
									type = nextToken(st);

								String s = name.substring(p);
								p = s.indexOf('.');
								String area = s.substring(0, p);
								String number = s.substring(p + 1);

								star = new LsStar(revision, area, number, coor_string, p_mag, type);
							}
						}
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("LSS")) {
					String p_mag = getMagnitude(nextToken(st));
					String type = nextToken(st);

					star = new LssStar(name.substring(3), coor_string, p_mag, type);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("LWD")) {
					String pg_mag = getMagnitude(nextToken(st));

					star = new LwdStar(name.substring(3), coor_string, pg_mag);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("Lanning")) {
					String b_mag = "";
					String u_b = "";
					String data = "";

					while (st.hasMoreTokens()) {
						String value = nextToken(st);
						if (value.indexOf("B=") == 0  &&  b_mag.length() == 0)
							b_mag = getValue(value);
						else if (value.indexOf("U-B=") == 0  &&  u_b.length() == 0)
							u_b = getValue(value);
						else {
							if (data.length() > 0)
								data += " ";
							data += value;
						}
					}

					star = new LanningStar(name.substring(7), coor_string, b_mag, u_b);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("MLA")) {
					String em = nextToken(st);
					String data = getData(st);

					String number = name.substring(3);
					if (number.indexOf(':') > 0)
						number = number.substring(0, number.length() - 1);

					star = new MlaStar(number, coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 5  &&  name.substring(0, 3).equals("NGC")  &&  name.indexOf(":KWBBe") > 0) {
					String object = name.substring(0, name.indexOf(":KWBBe"));
					String number = name.substring(name.indexOf(":KWBBe") + 6);

					String em = nextToken(st);
					String v_mag = getValue(nextToken(st));
					String data = getData(st);

					star = new KwbbeStar(object, number, coor_string, v_mag);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("OMHR_J")) {
					String u_mag = getValue(nextToken(st));
					String v_mag = getValue(nextToken(st));
					String data = getData(st);

					star = new OmhrStar(name.substring(6), coor_string, u_mag, v_mag);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("PB")) {
					boolean valid = true;
					for (int i = 2 ; i < name.length() ; i++) {
						if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
							valid = false;
					}

					if (valid) {
						String b_mag = getMagnitude(nextToken(st));
						String data = getData(st);

						star = new PbStar(name.substring(2), coor_string, b_mag);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("PHL")) {
					String p_mag = getMagnitude(nextToken(st));

					star = new PhlStar(name.substring(3), coor_string, p_mag);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("RJHA")) {
					String data = getData(st);

					star = new RjhaStar(name.substring(4), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("Sand")) {
					String v_mag = "";
					String b_v = "";
					String pmA = "";
					String pmD = "";
					while (st.hasMoreTokens()) {
						String s = nextToken(st);
						if (s.indexOf("V=") == 0) {
							v_mag = getValue(s);
						} else if (s.indexOf("B-V=") == 0) {
							b_v = getValue(s);
						} else if (s.indexOf("pmA=") == 0) {
							pmA = getValue(s);
						} else if (s.indexOf("pmD=") == 0) {
							pmD = getValue(s);
						}
					}

					star = new SandStar(name.substring(4), coor_string, v_mag, b_v, pmA, pmD);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("SBS")) {
					String mag = nextToken(st);
					String type = nextToken(st);
					String data = getData(st);

					if (type.equals("gal")) {
						star = new SbsStar("G", name.substring(3), coor_string, mag);
						((DefaultStar)star).setData(data);
					} else if (type.equals("stellar")) {
						star = new SbsStar("S", name.substring(3), coor_string, mag);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("UIT")) {
					String v_mag = getValue(nextToken(st));
					String u_b = getValue(nextToken(st));
					String data = getData(st);

					name = name.substring(3);
					while (name.charAt(0) == ('0'))
						name = name.substring(1);

					star = new Mbh96Star(name, coor_string, v_mag, u_b);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("UM")  &&
					'0' <= name.charAt(2)  &&  name.charAt(2) <= '9') {
					String data = getData(st);

					name = name.substring(2);
					while (name.charAt(0) == ('0'))
						name = name.substring(1);

					star = new UmStar(name, coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("US")  &&
					'0' <= name.charAt(2)  &&  name.charAt(2) <= '9') {
					String b_mag = getMagnitude(nextToken(st));
					String col = nextToken(st);

					if (col.indexOf("col=") == 0) {
						col = getValue(col);
						String data = getData(st);

						star = new UsStar(name.substring(2), coor_string, b_mag, col);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("VDBH")) {
					String data = getData(st);

					star = new VdbhStar(name.substring(4), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 10  &&  name.substring(0, 2).equals("WD")) {
					boolean valid = true;
					for (int i = 2 ; i < 10 ; i++) {
						if (i != 6  &&  ! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
							valid = false;
					}

					if (valid) {
						String mag = "";
						String data = "";
						if (st.hasMoreTokens())
							mag = nextToken(st);

						if (mag.length() > 0  &&  ! ('0' <= mag.charAt(0)  &&  mag.charAt(0) <= '9')) {
							data = mag;
							while (st.hasMoreTokens())
								data += " " + nextToken(st);
						}

						star = new WdStar(name.substring(2), coor_string, mag);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("Wg")) {
					boolean valid = true;
					for (int i = 2 ; i < name.length() ; i++) {
						if (! ('0' <= name.charAt(i)  &&  name.charAt(i) <= '9'))
							valid = false;
					}

					if (valid) {
						String v_mag = getMagnitude(nextToken(st));
						String data = getData(st);

						star = new WgStar(name.substring(2), coor_string, v_mag);
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 9  &&  name.substring(0, 5).equals("[A64]")) {
					String area = name.substring(5, 7);
					String number = name.substring(8);
					String b_mag = nextToken(st);
					String spectrum = "";
					if (st.hasMoreTokens()) {
						b_mag = getMagnitude(b_mag);
						spectrum = nextToken(st);
					} else {
						spectrum = b_mag;
						b_mag = "";
					}

					star = new A64Star(area, number, coor_string, b_mag, spectrum);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("[A72c]")) {
					String spectrum = nextToken(st);
					String data = getData(st);

					star = new A72cStar(name.substring(6), coor_string, spectrum);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("[B86]")) {
					String data = getData(st);

					star = new B86Star(name.substring(5), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[BBE90]")) {
					String j_mag = getMagnitude(nextToken(st));
					String type = nextToken(st);
					String data = getData(st);

					star = new Bbe90Star(name.substring(7), coor_string, j_mag, type);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[BFA97]")) {
					String fuv_mag = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new Bfa97Star(name.substring(7), coor_string, fuv_mag);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[CBB98]")) {
					String v_mag = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new Cbb98Star(name.substring(7), coor_string, v_mag);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[CTT83]")) {
					String r_mag = "";
					if (st.hasMoreTokens())
						r_mag = getMagnitude(nextToken(st));

					star = new Ctt83Star(name.substring(7), coor_string, r_mag);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("[DI91]")) {
					String v_mag = getValue(nextToken(st));
					String b_v = getValue(nextToken(st));

					star = new Di91Star(name.substring(6), coor_string, v_mag, b_v);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[HPJ88]")) {
					String v_mag = getValue(nextToken(st));
					String b_v = getValue(nextToken(st));

					star = new Hpj88Star(name.substring(7), coor_string, v_mag, b_v);
				}

				if (name.length() >= 8  &&  name.substring(0, 8).equals("[KP2001]")) {
					String b_mag = getMagnitude(nextToken(st));
					String spectrum = nextToken(st);

					star = new Kp2001Star(name.substring(8), coor_string, b_mag, spectrum);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("[MA93]")) {
					star = new Ma93Star(name.substring(6), coor_string);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("[MH95]")) {
					star = new Mh95Star(name.substring(6), coor_string);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("[MT91]")) {
					String v_mag = getValue(nextToken(st));
					String b_v = getValue(nextToken(st));
					String data = getData(st);

					star = new Mt91Star(name.substring(6), coor_string, v_mag, b_v);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("[OW94]")) {
					String data = getData(st);

					star = new Ow94Star(name.substring(6), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[RRW93]")) {
					String v_mag = getValue(nextToken(st));
					String spectrum = nextToken(st);

					star = new Rrw93Star(name.substring(7), coor_string, v_mag, spectrum);
				}

				if (name.length() >= 9  &&  name.substring(0, 9).equals("[MFL2000]")) {
					star = new Mfl2000Star(name.substring(9), coor_string);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("[W59]")) {
					String table = name.substring(5, 6);
					String number = name.substring(7);
					String i_mag = getMagnitude(nextToken(st));
					String spectrum = nextToken(st);

					star = new W59Star(table, number, coor_string, i_mag, spectrum);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("CFRS")) {
					String i_mag = getValue(nextToken(st));
					String v_i = getValue(nextToken(st));
					String z = getValue(nextToken(st));

					star = new CfrsStar(name.substring(4), coor_string, i_mag, v_i, z);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("CGCS_J")) {
					String alias = getMagnitude(nextToken(st));
					String b_mag = nextToken(st);
					String v_mag = nextToken(st);
					String ir_mag = nextToken(st);
					String data = getData(st);

					int cgcs = Integer.parseInt(alias.substring(4));

					star = new Cgcs3Star(name.substring(6), coor_string, cgcs, b_mag, v_mag, ir_mag);

					if (data.length() > 0  &&  data.equals("-") == false)
						((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("UHA")) {
					String v_mag = getMagnitude(nextToken(st));
					String b_mag = getMagnitude(nextToken(st));

					star = new UhaStar(name.substring(3), coor_string, b_mag, v_mag);
				}

				if (name.length() >= 3  &&  name.substring(0, 2).equals("DO")) {
					boolean flag = true;
					for (int i = 2 ; i < name.length() - 1 ; i++) {
						if (name.charAt(i) < '0'  ||  name.charAt(i) > '9')
							flag = false;
					}

					if (flag) {
						String v_mag = nextToken(st);
						String spectrum = "";
						if (st.hasMoreTokens()) {
							spectrum = nextToken(st);
						} else {
							spectrum = v_mag;
							v_mag = "";
						}

						star = new DoStar(name.substring(2), coor_string, v_mag, spectrum);
					}
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("KUG")) {
					String b_mag = getValue(nextToken(st));

					star = new KugStar(name.substring(3), coor_string, b_mag);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("Mrk")) {
					String mag = nextToken(st);
					String z = getValue(nextToken(st));

					star = new MrkStar(name.substring(3), coor_string, mag, z);
				}

				if (name.length() >= 4  &&  name.substring(0, 4).equals("PN_G")) {
					String data = getData(st);

					star = new PnGStar(name.substring(4), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 7  &&  name.substring(0, 7).equals("[BDF99]")) {
					star = new Bdf99Star(name.substring(7), coor_string);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("1AXG_J")) {
					String value = getMagnitude(nextToken(st));
					String hard = getValue(nextToken(st));

					star = new Axg1Star(name.substring(6), coor_string, value, hard);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("1RXSJ")) {
					if (st.countTokens() >= 2) {
						String v_mag = nextToken(st);
						String type = nextToken(st);
						String data = getData(st);

						star = new Rxs1Star(name.substring(5), coor_string, v_mag, type);
						((DefaultStar)star).setData(data);
					}
				}
				if (name.length() >= 6  &&  name.substring(0, 6).equals("1RXS_J")) {
					String v_mag = getValue(nextToken(st));
					String type = nextToken(st);
					String data = getData(st);

					star = new Rxs1Star(name.substring(6), coor_string, v_mag, type);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 3  &&  name.substring(0, 3).equals("RXJ")  &&
					(name.indexOf('+') > 0  ||  name.indexOf('-') > 0)) {
					String value = "";
					if (st.hasMoreTokens())
						value = nextToken(st);

					if (value.indexOf("V=") == 0) {
						String v_mag = getValue(value);
						String b_v = getValue(nextToken(st));
						String data = getData(st);

						star = new RxStar(name.substring(3), coor_string, v_mag, b_v);
						((DefaultStar)star).setData(data);
					} else if (value.length() > 0  &&  '0' <= value.charAt(0)  &&  value.charAt(0) <= '9') {
						String type = nextToken(st);
						if (st.hasMoreTokens()) {
							String max = value;
							String min = getMagnitude(type);
							String mag_system = getMagnitudeSystem(type);
							type = nextToken(st);

							star = new RosatStar(name.substring(3), coor_string, "", max, min, mag_system, type);
						} else {
							String mag = getMagnitude(value);
							String mag_system = getMagnitudeSystem(value);

							star = new RosatStar(name.substring(3), coor_string, mag, "", "", mag_system, type);
						}
					} else {
						String data = value;
						while (st.hasMoreTokens())
							data += " " + nextToken(st);

						star = new RxStar(name.substring(3), coor_string, "", "");
						((DefaultStar)star).setData(data);
					}
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("1SAX_J")) {
					String x_ray = getValue(nextToken(st));
					String type = getData(st);

					star = new Sax1Star(name.substring(6), coor_string, x_ray, type);
				}

				if (name.length() >= 6  &&  name.substring(0, 6).equals("2E_IPC")) {
					String type = nextToken(st);
					String data = getData(st);

					star = new E2Star(name.substring(6), coor_string);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("2RE_J")) {
					String s1 = getMagnitude(nextToken(st));
					String s2 = getMagnitude(nextToken(st));
					String data = getData(st);

					star = new Re2Star(name.substring(5), coor_string, s1, s2);
					((DefaultStar)star).setData(data);
				}

				if (name.length() >= 5  &&  name.substring(0, 5).equals("FAUST")) {
					String fuv = getValue(nextToken(st));
					String mag = "";
					String spectrum = nextToken(st);
					if (st.hasMoreTokens()) {
						mag = spectrum;
						spectrum = nextToken(st);
					}

					star = new FaustStar(name.substring(5), coor_string, fuv, mag, spectrum);
				}

				if (star == null) {
					String data = getData(st);

					star = new OtherVariableStar(name.replace('_', ' '), name, coor_string);
					((DefaultStar)star).setData(data);
				}
			}

			if (star != null) {
				if (center_coor != null) {
					ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
					Position position = cmf.mapCoordinatesToXY(star.getCoor());
					star.setPosition(position);
				}
			}

			return star;
		} catch ( Exception exception ) {
			System.err.println(record);
			System.err.println(exception);
		}

		return null;
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The list of the undesignated new variable stars and remarkable <br>";
		html += "stars in the papers, compiled by Taichi Kato.<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://vsnet.kusastro.kyoto-u.ac.jp/pub/vsnet/others/newvar.cat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">http://www.aerith.net/misao/public/newvar2.cat</font></u> (additional stars)";
		html += "<u><font color=\"#0000ff\">http://www.aerith.net/misao/public/newvar-misao.cat</font></u> (additional stars)";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
