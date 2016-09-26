CONTROL AUTOMATON ErrorPath1

INITIAL STATE ARG0;

STATE USEFIRST ARG0 :
    MATCH "" -> GOTO ARG1;
    TRUE -> STOP;

STATE USEFIRST ARG1 :
    MATCH "Main_cleanupTimeShifts" -> GOTO ARG2;
    TRUE -> STOP;

STATE USEFIRST ARG2 :
    MATCH "" -> GOTO ARG3;
    TRUE -> STOP;

STATE USEFIRST ARG3 :
    MATCH "randomSequenceOfActions(3);\n" -> GOTO ARG4;
    TRUE -> STOP;

STATE USEFIRST ARG4 :
    MATCH "" -> GOTO ARG5;
    TRUE -> STOP;

STATE USEFIRST ARG5 :
    MATCH "Actions a=new Actions();\n" -> GOTO ARG6;
    TRUE -> STOP;

STATE USEFIRST ARG6 :
    MATCH "Actions a=new Actions();\n" -> GOTO ARG7;
    TRUE -> STOP;

STATE USEFIRST ARG7 :
    MATCH "" -> GOTO ARG8;
    TRUE -> STOP;

STATE USEFIRST ARG8 :
    MATCH " boolean Actions_methAndRunningLastTime = false;" -> GOTO ARG9;
    TRUE -> STOP;

STATE USEFIRST ARG9 :
    MATCH " MinePumpSystem.Environment Actions_env;" -> GOTO ARG10;
    TRUE -> STOP;

STATE USEFIRST ARG10 :
    MATCH " boolean Actions_switchedOnBeforeTS = false;" -> GOTO ARG11;
    TRUE -> STOP;

STATE USEFIRST ARG11 :
    MATCH " MinePumpSystem.MinePump Actions_p;" -> GOTO ARG12;
    TRUE -> STOP;

STATE USEFIRST ARG12 :
    MATCH "env=new Environment();\n" -> GOTO ARG13;
    TRUE -> STOP;

STATE USEFIRST ARG13 :
    MATCH "" -> GOTO ARG14;
    TRUE -> STOP;

STATE USEFIRST ARG14 :
    MATCH "private int MinePumpSystem.Environment_waterLevel = 1;" -> GOTO ARG15;
    TRUE -> STOP;

STATE USEFIRST ARG15 :
    MATCH "private boolean MinePumpSystem.Environment_methaneLevelCritical = false;" -> GOTO ARG16;
    TRUE -> STOP;

STATE USEFIRST ARG16 :
    MATCH "" -> GOTO ARG17;
    TRUE -> STOP;

STATE USEFIRST ARG17 :
    MATCH "" -> GOTO ARG18;
    TRUE -> STOP;

STATE USEFIRST ARG18 :
    MATCH "p=new MinePump(env);\n" -> GOTO ARG19;
    TRUE -> STOP;

STATE USEFIRST ARG19 :
    MATCH "" -> GOTO ARG20;
    TRUE -> STOP;

STATE USEFIRST ARG20 :
    MATCH " boolean MinePumpSystem.MinePump_pumpRunning = false;" -> GOTO ARG21;
    TRUE -> STOP;

STATE USEFIRST ARG21 :
    MATCH " MinePumpSystem.Environment MinePumpSystem.MinePump_env;" -> GOTO ARG22;
    TRUE -> STOP;

STATE USEFIRST ARG22 :
    MATCH " boolean MinePumpSystem.MinePump_systemActive = true;" -> GOTO ARG23;
    TRUE -> STOP;

STATE USEFIRST ARG23 :
    MATCH "super();\n" -> GOTO ARG24;
    TRUE -> STOP;

STATE USEFIRST ARG24 :
    MATCH "this.env=env;\n" -> GOTO ARG25;
    TRUE -> STOP;

STATE USEFIRST ARG25 :
    MATCH "" -> GOTO ARG26;
    TRUE -> STOP;

STATE USEFIRST ARG26 :
    MATCH "" -> GOTO ARG27;
    TRUE -> STOP;

STATE USEFIRST ARG27 :
    MATCH "" -> GOTO ARG28;
    TRUE -> STOP;

STATE USEFIRST ARG28 :
    MATCH "" -> GOTO ARG29;
    TRUE -> STOP;

STATE USEFIRST ARG29 :
    MATCH "Actions a=new Actions();\n" -> GOTO ARG30;
    TRUE -> STOP;

STATE USEFIRST ARG30 :
    MATCH "int counter=0;\n" -> GOTO ARG31;
    TRUE -> STOP;

STATE USEFIRST ARG31 :
    MATCH "" -> GOTO ARG32;
    TRUE -> STOP;

STATE USEFIRST ARG32 :
    MATCH "[counter < maxLength]" -> GOTO ARG33;
    TRUE -> STOP;

STATE USEFIRST ARG33 :
    MATCH "counter++;\n" -> GOTO ARG34;
    TRUE -> STOP;

STATE USEFIRST ARG34 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG35;
    TRUE -> STOP;

STATE USEFIRST ARG35 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG36;
    TRUE -> STOP;

STATE USEFIRST ARG36 :
    MATCH "" -> GOTO ARG37;
    TRUE -> STOP;

STATE USEFIRST ARG37 :
    MATCH "Random random=new Random();\n" -> GOTO ARG38;
    TRUE -> STOP;

STATE USEFIRST ARG38 :
    MATCH "Random random=new Random();\n" -> GOTO ARG39;
    TRUE -> STOP;

STATE USEFIRST ARG39 :
    MATCH "Random random=new Random();\n" -> GOTO ARG40;
    TRUE -> STOP;

STATE USEFIRST ARG40 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG41;
    TRUE -> STOP;

STATE USEFIRST ARG41 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG42;
    TRUE -> STOP;

STATE USEFIRST ARG42 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG43;
    TRUE -> STOP;

STATE USEFIRST ARG43 :
    MATCH "" -> GOTO ARG44;
    TRUE -> STOP;

STATE USEFIRST ARG44 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG45;
    TRUE -> STOP;

STATE USEFIRST ARG45 :
    MATCH "[__CPAchecker_TMP_1]" -> GOTO ARG47;
    TRUE -> STOP;

STATE USEFIRST ARG47 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG48;
    TRUE -> STOP;

STATE USEFIRST ARG48 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG49;
    TRUE -> STOP;

STATE USEFIRST ARG49 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG50;
    TRUE -> STOP;

STATE USEFIRST ARG50 :
    MATCH "" -> GOTO ARG51;
    TRUE -> STOP;

STATE USEFIRST ARG51 :
    MATCH "Random random=new Random();\n" -> GOTO ARG52;
    TRUE -> STOP;

STATE USEFIRST ARG52 :
    MATCH "Random random=new Random();\n" -> GOTO ARG53;
    TRUE -> STOP;

STATE USEFIRST ARG53 :
    MATCH "Random random=new Random();\n" -> GOTO ARG54;
    TRUE -> STOP;

STATE USEFIRST ARG54 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG55;
    TRUE -> STOP;

STATE USEFIRST ARG55 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG56;
    TRUE -> STOP;

STATE USEFIRST ARG56 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG57;
    TRUE -> STOP;

STATE USEFIRST ARG57 :
    MATCH "" -> GOTO ARG58;
    TRUE -> STOP;

STATE USEFIRST ARG58 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG59;
    TRUE -> STOP;

STATE USEFIRST ARG59 :
    MATCH "[__CPAchecker_TMP_2]" -> GOTO ARG61;
    TRUE -> STOP;

STATE USEFIRST ARG61 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG62;
    TRUE -> STOP;

STATE USEFIRST ARG62 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG63;
    TRUE -> STOP;

STATE USEFIRST ARG63 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG64;
    TRUE -> STOP;

STATE USEFIRST ARG64 :
    MATCH "" -> GOTO ARG65;
    TRUE -> STOP;

STATE USEFIRST ARG65 :
    MATCH "Random random=new Random();\n" -> GOTO ARG66;
    TRUE -> STOP;

STATE USEFIRST ARG66 :
    MATCH "Random random=new Random();\n" -> GOTO ARG67;
    TRUE -> STOP;

STATE USEFIRST ARG67 :
    MATCH "Random random=new Random();\n" -> GOTO ARG68;
    TRUE -> STOP;

STATE USEFIRST ARG68 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG69;
    TRUE -> STOP;

STATE USEFIRST ARG69 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG70;
    TRUE -> STOP;

STATE USEFIRST ARG70 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG71;
    TRUE -> STOP;

STATE USEFIRST ARG71 :
    MATCH "" -> GOTO ARG72;
    TRUE -> STOP;

STATE USEFIRST ARG72 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG73;
    TRUE -> STOP;

STATE USEFIRST ARG73 :
    MATCH "[__CPAchecker_TMP_3]" -> GOTO ARG75;
    TRUE -> STOP;

STATE USEFIRST ARG75 :
    MATCH "__CPAchecker_TMP_3" -> GOTO ARG76;
    TRUE -> STOP;

STATE USEFIRST ARG76 :
    MATCH "boolean action4=false;\n" -> GOTO ARG77;
    TRUE -> STOP;

STATE USEFIRST ARG77 :
    MATCH "[!(!action3)]" -> GOTO ARG78;
    TRUE -> STOP;

STATE USEFIRST ARG78 :
    MATCH "[action1]" -> GOTO ARG79;
    TRUE -> STOP;

STATE USEFIRST ARG79 :
    MATCH "a.waterRise();\n" -> GOTO ARG80;
    TRUE -> STOP;

STATE USEFIRST ARG80 :
    MATCH "" -> GOTO ARG81;
    TRUE -> STOP;

STATE USEFIRST ARG81 :
    MATCH "env.waterRise();\n" -> GOTO ARG82;
    TRUE -> STOP;

STATE USEFIRST ARG82 :
    MATCH "" -> GOTO ARG83;
    TRUE -> STOP;

STATE USEFIRST ARG83 :
    MATCH "switch (waterLevel)" -> GOTO ARG84;
    TRUE -> STOP;

STATE USEFIRST ARG84 :
    MATCH "[!(MinePumpSystem.Environment_waterLevel == 0)]" -> GOTO ARG85;
    TRUE -> STOP;

STATE USEFIRST ARG85 :
    MATCH "[MinePumpSystem.Environment_waterLevel == 1]" -> GOTO ARG86;
    TRUE -> STOP;

STATE USEFIRST ARG86 :
    MATCH "waterLevel=2;\n" -> GOTO ARG87;
    TRUE -> STOP;

STATE USEFIRST ARG87 :
    MATCH "break;\n" -> GOTO ARG88;
    TRUE -> STOP;

STATE USEFIRST ARG88 :
    MATCH "" -> GOTO ARG89;
    TRUE -> STOP;

STATE USEFIRST ARG89 :
    MATCH "" -> GOTO ARG90;
    TRUE -> STOP;

STATE USEFIRST ARG90 :
    MATCH "" -> GOTO ARG91;
    TRUE -> STOP;

STATE USEFIRST ARG91 :
    MATCH "" -> GOTO ARG92;
    TRUE -> STOP;

STATE USEFIRST ARG92 :
    MATCH "" -> GOTO ARG93;
    TRUE -> STOP;

STATE USEFIRST ARG93 :
    MATCH "[action2]" -> GOTO ARG94;
    TRUE -> STOP;

STATE USEFIRST ARG94 :
    MATCH "a.methaneChange();\n" -> GOTO ARG95;
    TRUE -> STOP;

STATE USEFIRST ARG95 :
    MATCH "" -> GOTO ARG96;
    TRUE -> STOP;

STATE USEFIRST ARG96 :
    MATCH "env.changeMethaneLevel();\n" -> GOTO ARG97;
    TRUE -> STOP;

STATE USEFIRST ARG97 :
    MATCH "" -> GOTO ARG98;
    TRUE -> STOP;

STATE USEFIRST ARG98 :
    MATCH "[!MinePumpSystem.Environment_methaneLevelCritical]" -> GOTO ARG99;
    TRUE -> STOP;

STATE USEFIRST ARG99 :
    MATCH "!MinePumpSystem.Environment_methaneLevelCritical" -> GOTO ARG100;
    TRUE -> STOP;

STATE USEFIRST ARG100 :
    MATCH "" -> GOTO ARG101;
    TRUE -> STOP;

STATE USEFIRST ARG101 :
    MATCH "" -> GOTO ARG102;
    TRUE -> STOP;

STATE USEFIRST ARG102 :
    MATCH "" -> GOTO ARG103;
    TRUE -> STOP;

STATE USEFIRST ARG103 :
    MATCH "" -> GOTO ARG104;
    TRUE -> STOP;

STATE USEFIRST ARG104 :
    MATCH "" -> GOTO ARG105;
    TRUE -> STOP;

STATE USEFIRST ARG105 :
    MATCH "[action3]" -> GOTO ARG106;
    TRUE -> STOP;

STATE USEFIRST ARG106 :
    MATCH "a.startSystem();\n" -> GOTO ARG107;
    TRUE -> STOP;

STATE USEFIRST ARG107 :
    MATCH "" -> GOTO ARG108;
    TRUE -> STOP;

STATE USEFIRST ARG108 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG109;
    TRUE -> STOP;

STATE USEFIRST ARG109 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG110;
    TRUE -> STOP;

STATE USEFIRST ARG110 :
    MATCH "" -> GOTO ARG111;
    TRUE -> STOP;

STATE USEFIRST ARG111 :
    MATCH "return systemActive;\n" -> GOTO ARG112;
    TRUE -> STOP;

STATE USEFIRST ARG112 :
    MATCH "" -> GOTO ARG113;
    TRUE -> STOP;

STATE USEFIRST ARG113 :
    MATCH "[!(!__CPAchecker_TMP_0)]" -> GOTO ARG114;
    TRUE -> STOP;

STATE USEFIRST ARG114 :
    MATCH "" -> GOTO ARG115;
    TRUE -> STOP;

STATE USEFIRST ARG115 :
    MATCH "" -> GOTO ARG116;
    TRUE -> STOP;

STATE USEFIRST ARG116 :
    MATCH "" -> GOTO ARG117;
    TRUE -> STOP;

STATE USEFIRST ARG117 :
    MATCH "a.timeShift();\n" -> GOTO ARG118;
    TRUE -> STOP;

STATE USEFIRST ARG118 :
    MATCH "" -> GOTO ARG119;
    TRUE -> STOP;

STATE USEFIRST ARG119 :
    MATCH "p.timeShift();\n" -> GOTO ARG120;
    TRUE -> STOP;

STATE USEFIRST ARG120 :
    MATCH "" -> GOTO ARG121;
    TRUE -> STOP;

STATE USEFIRST ARG121 :
    MATCH "[!(MinePumpSystem.MinePump_pumpRunning)]" -> GOTO ARG122;
    TRUE -> STOP;

STATE USEFIRST ARG122 :
    MATCH "[MinePumpSystem.MinePump_systemActive]" -> GOTO ARG123;
    TRUE -> STOP;

STATE USEFIRST ARG123 :
    MATCH "processEnvironment();\n" -> GOTO ARG124;
    TRUE -> STOP;

STATE USEFIRST ARG124 :
    MATCH "" -> GOTO ARG125;
    TRUE -> STOP;

STATE USEFIRST ARG125 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG126;
    TRUE -> STOP;

STATE USEFIRST ARG126 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG127;
    TRUE -> STOP;

STATE USEFIRST ARG127 :
    MATCH "" -> GOTO ARG128;
    TRUE -> STOP;

STATE USEFIRST ARG128 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG129;
    TRUE -> STOP;

STATE USEFIRST ARG129 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG130;
    TRUE -> STOP;

STATE USEFIRST ARG130 :
    MATCH "" -> GOTO ARG131;
    TRUE -> STOP;

STATE USEFIRST ARG131 :
    MATCH "return waterLevel != 2;\n" -> GOTO ARG132;
    TRUE -> STOP;

STATE USEFIRST ARG132 :
    MATCH "" -> GOTO ARG133;
    TRUE -> STOP;

STATE USEFIRST ARG133 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG134;
    TRUE -> STOP;

STATE USEFIRST ARG134 :
    MATCH "" -> GOTO ARG135;
    TRUE -> STOP;

STATE USEFIRST ARG135 :
    MATCH "[!MinePumpSystem.MinePump_pumpRunning]" -> GOTO ARG136;
    TRUE -> STOP;

STATE USEFIRST ARG136 :
    MATCH "[__CPAchecker_TMP_0]" -> GOTO ARG137;
    TRUE -> STOP;

STATE USEFIRST ARG137 :
    MATCH "activatePump();\n" -> GOTO ARG138;
    TRUE -> STOP;

STATE USEFIRST ARG138 :
    MATCH "" -> GOTO ARG139;
    TRUE -> STOP;

STATE USEFIRST ARG139 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG140;
    TRUE -> STOP;

STATE USEFIRST ARG140 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG141;
    TRUE -> STOP;

STATE USEFIRST ARG141 :
    MATCH "" -> GOTO ARG142;
    TRUE -> STOP;

STATE USEFIRST ARG142 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG143;
    TRUE -> STOP;

STATE USEFIRST ARG143 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG144;
    TRUE -> STOP;

STATE USEFIRST ARG144 :
    MATCH "" -> GOTO ARG145;
    TRUE -> STOP;

STATE USEFIRST ARG145 :
    MATCH "return methaneLevelCritical;\n" -> GOTO ARG146;
    TRUE -> STOP;

STATE USEFIRST ARG146 :
    MATCH "" -> GOTO ARG147;
    TRUE -> STOP;

STATE USEFIRST ARG147 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG148;
    TRUE -> STOP;

STATE USEFIRST ARG148 :
    MATCH "" -> GOTO ARG149;
    TRUE -> STOP;

STATE USEFIRST ARG149 :
    MATCH "[!(!__CPAchecker_TMP_0)]" -> GOTO ARG150;
    TRUE -> STOP;

STATE USEFIRST ARG150 :
    MATCH "" -> GOTO ARG151;
    TRUE -> STOP;

STATE USEFIRST ARG151 :
    MATCH "" -> GOTO ARG152;
    TRUE -> STOP;

STATE USEFIRST ARG152 :
    MATCH "" -> GOTO ARG153;
    TRUE -> STOP;

STATE USEFIRST ARG153 :
    MATCH "processEnvironment__wrappee__base();\n" -> GOTO ARG154;
    TRUE -> STOP;

STATE USEFIRST ARG154 :
    MATCH "" -> GOTO ARG155;
    TRUE -> STOP;

STATE USEFIRST ARG155 :
    MATCH "" -> GOTO ARG156;
    TRUE -> STOP;

STATE USEFIRST ARG156 :
    MATCH "" -> GOTO ARG157;
    TRUE -> STOP;

STATE USEFIRST ARG157 :
    MATCH "" -> GOTO ARG158;
    TRUE -> STOP;

STATE USEFIRST ARG158 :
    MATCH "" -> GOTO ARG159;
    TRUE -> STOP;

STATE USEFIRST ARG159 :
    MATCH "" -> GOTO ARG160;
    TRUE -> STOP;

STATE USEFIRST ARG160 :
    MATCH "" -> GOTO ARG161;
    TRUE -> STOP;

STATE USEFIRST ARG161 :
    MATCH "" -> GOTO ARG162;
    TRUE -> STOP;

STATE USEFIRST ARG162 :
    MATCH "" -> GOTO ARG163;
    TRUE -> STOP;

STATE USEFIRST ARG163 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG164;
    TRUE -> STOP;

STATE USEFIRST ARG164 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG165;
    TRUE -> STOP;

STATE USEFIRST ARG165 :
    MATCH "" -> GOTO ARG166;
    TRUE -> STOP;

STATE USEFIRST ARG166 :
    MATCH "return systemActive;\n" -> GOTO ARG167;
    TRUE -> STOP;

STATE USEFIRST ARG167 :
    MATCH "" -> GOTO ARG168;
    TRUE -> STOP;

STATE USEFIRST ARG168 :
    MATCH "[__CPAchecker_TMP_0]" -> GOTO ARG169;
    TRUE -> STOP;

STATE USEFIRST ARG169 :
    MATCH "Specification1();\n" -> GOTO ARG170;
    TRUE -> STOP;

STATE USEFIRST ARG170 :
    MATCH "" -> GOTO ARG171;
    TRUE -> STOP;

STATE USEFIRST ARG171 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG172;
    TRUE -> STOP;

STATE USEFIRST ARG172 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG173;
    TRUE -> STOP;

STATE USEFIRST ARG173 :
    MATCH "" -> GOTO ARG174;
    TRUE -> STOP;

STATE USEFIRST ARG174 :
    MATCH "return env;\n" -> GOTO ARG175;
    TRUE -> STOP;

STATE USEFIRST ARG175 :
    MATCH "" -> GOTO ARG176;
    TRUE -> STOP;

STATE USEFIRST ARG176 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG177;
    TRUE -> STOP;

STATE USEFIRST ARG177 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG178;
    TRUE -> STOP;

STATE USEFIRST ARG178 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG179;
    TRUE -> STOP;

STATE USEFIRST ARG179 :
    MATCH "" -> GOTO ARG180;
    TRUE -> STOP;

STATE USEFIRST ARG180 :
    MATCH "return methaneLevelCritical;\n" -> GOTO ARG181;
    TRUE -> STOP;

STATE USEFIRST ARG181 :
    MATCH "" -> GOTO ARG182;
    TRUE -> STOP;

STATE USEFIRST ARG182 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG183;
    TRUE -> STOP;

STATE USEFIRST ARG183 :
    MATCH "[__CPAchecker_TMP_1]" -> GOTO ARG184;
    TRUE -> STOP;

STATE USEFIRST ARG184 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG185;
    TRUE -> STOP;

STATE USEFIRST ARG185 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG186;
    TRUE -> STOP;

STATE USEFIRST ARG186 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG187;
    TRUE -> STOP;

STATE USEFIRST ARG187 :
    MATCH "" -> GOTO ARG188;
    TRUE -> STOP;

STATE USEFIRST ARG188 :
    MATCH "return pumpRunning;\n" -> GOTO ARG189;
    TRUE -> STOP;

STATE USEFIRST ARG189 :
    MATCH "" -> GOTO ARG190;
    TRUE -> STOP;

STATE USEFIRST ARG190 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG191;
    TRUE -> STOP;

STATE USEFIRST ARG191 :
    MATCH "[!(__CPAchecker_TMP_2)]" -> GOTO ARG192;
    TRUE -> STOP;

STATE USEFIRST ARG192 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG193;
    TRUE -> STOP;

STATE USEFIRST ARG193 :
    MATCH "[b1]" -> GOTO ARG194;
    TRUE -> STOP;

STATE USEFIRST ARG194 :
    MATCH "[!(b2)]" -> GOTO ARG195;
    TRUE -> STOP;

STATE USEFIRST ARG195 :
    MATCH "" -> GOTO ARG196;
    TRUE -> STOP;

STATE USEFIRST ARG196 :
    MATCH "" -> GOTO ARG197;
    TRUE -> STOP;

STATE USEFIRST ARG197 :
    MATCH "" -> GOTO ARG198;
    TRUE -> STOP;

STATE USEFIRST ARG198 :
    MATCH "" -> GOTO ARG199;
    TRUE -> STOP;

STATE USEFIRST ARG199 :
    MATCH "" -> GOTO ARG200;
    TRUE -> STOP;

STATE USEFIRST ARG200 :
    MATCH "" -> GOTO ARG201;
    TRUE -> STOP;

STATE USEFIRST ARG201 :
    MATCH "[counter < maxLength]" -> GOTO ARG202;
    TRUE -> STOP;

STATE USEFIRST ARG202 :
    MATCH "counter++;\n" -> GOTO ARG203;
    TRUE -> STOP;

STATE USEFIRST ARG203 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG204;
    TRUE -> STOP;

STATE USEFIRST ARG204 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG205;
    TRUE -> STOP;

STATE USEFIRST ARG205 :
    MATCH "" -> GOTO ARG206;
    TRUE -> STOP;

STATE USEFIRST ARG206 :
    MATCH "Random random=new Random();\n" -> GOTO ARG207;
    TRUE -> STOP;

STATE USEFIRST ARG207 :
    MATCH "Random random=new Random();\n" -> GOTO ARG208;
    TRUE -> STOP;

STATE USEFIRST ARG208 :
    MATCH "Random random=new Random();\n" -> GOTO ARG209;
    TRUE -> STOP;

STATE USEFIRST ARG209 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG210;
    TRUE -> STOP;

STATE USEFIRST ARG210 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG211;
    TRUE -> STOP;

STATE USEFIRST ARG211 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG212;
    TRUE -> STOP;

STATE USEFIRST ARG212 :
    MATCH "" -> GOTO ARG213;
    TRUE -> STOP;

STATE USEFIRST ARG213 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG214;
    TRUE -> STOP;

STATE USEFIRST ARG214 :
    MATCH "[__CPAchecker_TMP_1]" -> GOTO ARG216;
    TRUE -> STOP;

STATE USEFIRST ARG216 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG217;
    TRUE -> STOP;

STATE USEFIRST ARG217 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG218;
    TRUE -> STOP;

STATE USEFIRST ARG218 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG219;
    TRUE -> STOP;

STATE USEFIRST ARG219 :
    MATCH "" -> GOTO ARG220;
    TRUE -> STOP;

STATE USEFIRST ARG220 :
    MATCH "Random random=new Random();\n" -> GOTO ARG221;
    TRUE -> STOP;

STATE USEFIRST ARG221 :
    MATCH "Random random=new Random();\n" -> GOTO ARG222;
    TRUE -> STOP;

STATE USEFIRST ARG222 :
    MATCH "Random random=new Random();\n" -> GOTO ARG223;
    TRUE -> STOP;

STATE USEFIRST ARG223 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG224;
    TRUE -> STOP;

STATE USEFIRST ARG224 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG225;
    TRUE -> STOP;

STATE USEFIRST ARG225 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG226;
    TRUE -> STOP;

STATE USEFIRST ARG226 :
    MATCH "" -> GOTO ARG227;
    TRUE -> STOP;

STATE USEFIRST ARG227 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG228;
    TRUE -> STOP;

STATE USEFIRST ARG228 :
    MATCH "[__CPAchecker_TMP_2]" -> GOTO ARG230;
    TRUE -> STOP;

STATE USEFIRST ARG230 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG231;
    TRUE -> STOP;

STATE USEFIRST ARG231 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG232;
    TRUE -> STOP;

STATE USEFIRST ARG232 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG233;
    TRUE -> STOP;

STATE USEFIRST ARG233 :
    MATCH "" -> GOTO ARG234;
    TRUE -> STOP;

STATE USEFIRST ARG234 :
    MATCH "Random random=new Random();\n" -> GOTO ARG235;
    TRUE -> STOP;

STATE USEFIRST ARG235 :
    MATCH "Random random=new Random();\n" -> GOTO ARG236;
    TRUE -> STOP;

STATE USEFIRST ARG236 :
    MATCH "Random random=new Random();\n" -> GOTO ARG237;
    TRUE -> STOP;

STATE USEFIRST ARG237 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG238;
    TRUE -> STOP;

STATE USEFIRST ARG238 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG239;
    TRUE -> STOP;

STATE USEFIRST ARG239 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG240;
    TRUE -> STOP;

STATE USEFIRST ARG240 :
    MATCH "" -> GOTO ARG241;
    TRUE -> STOP;

STATE USEFIRST ARG241 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG242;
    TRUE -> STOP;

STATE USEFIRST ARG242 :
    MATCH "[__CPAchecker_TMP_3]" -> GOTO ARG244;
    TRUE -> STOP;

STATE USEFIRST ARG244 :
    MATCH "__CPAchecker_TMP_3" -> GOTO ARG245;
    TRUE -> STOP;

STATE USEFIRST ARG245 :
    MATCH "boolean action4=false;\n" -> GOTO ARG246;
    TRUE -> STOP;

STATE USEFIRST ARG246 :
    MATCH "[!(!action3)]" -> GOTO ARG247;
    TRUE -> STOP;

STATE USEFIRST ARG247 :
    MATCH "[action1]" -> GOTO ARG248;
    TRUE -> STOP;

STATE USEFIRST ARG248 :
    MATCH "a.waterRise();\n" -> GOTO ARG249;
    TRUE -> STOP;

STATE USEFIRST ARG249 :
    MATCH "" -> GOTO ARG250;
    TRUE -> STOP;

STATE USEFIRST ARG250 :
    MATCH "env.waterRise();\n" -> GOTO ARG251;
    TRUE -> STOP;

STATE USEFIRST ARG251 :
    MATCH "" -> GOTO ARG252;
    TRUE -> STOP;

STATE USEFIRST ARG252 :
    MATCH "switch (waterLevel)" -> GOTO ARG253;
    TRUE -> STOP;

STATE USEFIRST ARG253 :
    MATCH "[!(MinePumpSystem.Environment_waterLevel == 0)]" -> GOTO ARG254;
    TRUE -> STOP;

STATE USEFIRST ARG254 :
    MATCH "[!(MinePumpSystem.Environment_waterLevel == 1)]" -> GOTO ARG255;
    TRUE -> STOP;

STATE USEFIRST ARG255 :
    MATCH "" -> GOTO ARG256;
    TRUE -> STOP;

STATE USEFIRST ARG256 :
    MATCH "" -> GOTO ARG257;
    TRUE -> STOP;

STATE USEFIRST ARG257 :
    MATCH "" -> GOTO ARG258;
    TRUE -> STOP;

STATE USEFIRST ARG258 :
    MATCH "" -> GOTO ARG259;
    TRUE -> STOP;

STATE USEFIRST ARG259 :
    MATCH "" -> GOTO ARG260;
    TRUE -> STOP;

STATE USEFIRST ARG260 :
    MATCH "" -> GOTO ARG261;
    TRUE -> STOP;

STATE USEFIRST ARG261 :
    MATCH "[action2]" -> GOTO ARG262;
    TRUE -> STOP;

STATE USEFIRST ARG262 :
    MATCH "a.methaneChange();\n" -> GOTO ARG263;
    TRUE -> STOP;

STATE USEFIRST ARG263 :
    MATCH "" -> GOTO ARG264;
    TRUE -> STOP;

STATE USEFIRST ARG264 :
    MATCH "env.changeMethaneLevel();\n" -> GOTO ARG265;
    TRUE -> STOP;

STATE USEFIRST ARG265 :
    MATCH "" -> GOTO ARG266;
    TRUE -> STOP;

STATE USEFIRST ARG266 :
    MATCH "[!(!MinePumpSystem.Environment_methaneLevelCritical)]" -> GOTO ARG267;
    TRUE -> STOP;

STATE USEFIRST ARG267 :
    MATCH "!MinePumpSystem.Environment_methaneLevelCritical" -> GOTO ARG268;
    TRUE -> STOP;

STATE USEFIRST ARG268 :
    MATCH "" -> GOTO ARG269;
    TRUE -> STOP;

STATE USEFIRST ARG269 :
    MATCH "" -> GOTO ARG270;
    TRUE -> STOP;

STATE USEFIRST ARG270 :
    MATCH "" -> GOTO ARG271;
    TRUE -> STOP;

STATE USEFIRST ARG271 :
    MATCH "" -> GOTO ARG272;
    TRUE -> STOP;

STATE USEFIRST ARG272 :
    MATCH "" -> GOTO ARG273;
    TRUE -> STOP;

STATE USEFIRST ARG273 :
    MATCH "[action3]" -> GOTO ARG274;
    TRUE -> STOP;

STATE USEFIRST ARG274 :
    MATCH "a.startSystem();\n" -> GOTO ARG275;
    TRUE -> STOP;

STATE USEFIRST ARG275 :
    MATCH "" -> GOTO ARG276;
    TRUE -> STOP;

STATE USEFIRST ARG276 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG277;
    TRUE -> STOP;

STATE USEFIRST ARG277 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG278;
    TRUE -> STOP;

STATE USEFIRST ARG278 :
    MATCH "" -> GOTO ARG279;
    TRUE -> STOP;

STATE USEFIRST ARG279 :
    MATCH "return systemActive;\n" -> GOTO ARG280;
    TRUE -> STOP;

STATE USEFIRST ARG280 :
    MATCH "" -> GOTO ARG281;
    TRUE -> STOP;

STATE USEFIRST ARG281 :
    MATCH "[!(!__CPAchecker_TMP_0)]" -> GOTO ARG282;
    TRUE -> STOP;

STATE USEFIRST ARG282 :
    MATCH "" -> GOTO ARG283;
    TRUE -> STOP;

STATE USEFIRST ARG283 :
    MATCH "" -> GOTO ARG284;
    TRUE -> STOP;

STATE USEFIRST ARG284 :
    MATCH "" -> GOTO ARG285;
    TRUE -> STOP;

STATE USEFIRST ARG285 :
    MATCH "a.timeShift();\n" -> GOTO ARG286;
    TRUE -> STOP;

STATE USEFIRST ARG286 :
    MATCH "" -> GOTO ARG287;
    TRUE -> STOP;

STATE USEFIRST ARG287 :
    MATCH "p.timeShift();\n" -> GOTO ARG288;
    TRUE -> STOP;

STATE USEFIRST ARG288 :
    MATCH "" -> GOTO ARG289;
    TRUE -> STOP;

STATE USEFIRST ARG289 :
    MATCH "[!(MinePumpSystem.MinePump_pumpRunning)]" -> GOTO ARG290;
    TRUE -> STOP;

STATE USEFIRST ARG290 :
    MATCH "[MinePumpSystem.MinePump_systemActive]" -> GOTO ARG291;
    TRUE -> STOP;

STATE USEFIRST ARG291 :
    MATCH "processEnvironment();\n" -> GOTO ARG292;
    TRUE -> STOP;

STATE USEFIRST ARG292 :
    MATCH "" -> GOTO ARG293;
    TRUE -> STOP;

STATE USEFIRST ARG293 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG294;
    TRUE -> STOP;

STATE USEFIRST ARG294 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG295;
    TRUE -> STOP;

STATE USEFIRST ARG295 :
    MATCH "" -> GOTO ARG296;
    TRUE -> STOP;

STATE USEFIRST ARG296 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG297;
    TRUE -> STOP;

STATE USEFIRST ARG297 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG298;
    TRUE -> STOP;

STATE USEFIRST ARG298 :
    MATCH "" -> GOTO ARG299;
    TRUE -> STOP;

STATE USEFIRST ARG299 :
    MATCH "return waterLevel != 2;\n" -> GOTO ARG300;
    TRUE -> STOP;

STATE USEFIRST ARG300 :
    MATCH "" -> GOTO ARG301;
    TRUE -> STOP;

STATE USEFIRST ARG301 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG302;
    TRUE -> STOP;

STATE USEFIRST ARG302 :
    MATCH "" -> GOTO ARG303;
    TRUE -> STOP;

STATE USEFIRST ARG303 :
    MATCH "[!MinePumpSystem.MinePump_pumpRunning]" -> GOTO ARG304;
    TRUE -> STOP;

STATE USEFIRST ARG304 :
    MATCH "[__CPAchecker_TMP_0]" -> GOTO ARG305;
    TRUE -> STOP;

STATE USEFIRST ARG305 :
    MATCH "activatePump();\n" -> GOTO ARG306;
    TRUE -> STOP;

STATE USEFIRST ARG306 :
    MATCH "" -> GOTO ARG307;
    TRUE -> STOP;

STATE USEFIRST ARG307 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG308;
    TRUE -> STOP;

STATE USEFIRST ARG308 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG309;
    TRUE -> STOP;

STATE USEFIRST ARG309 :
    MATCH "" -> GOTO ARG310;
    TRUE -> STOP;

STATE USEFIRST ARG310 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG311;
    TRUE -> STOP;

STATE USEFIRST ARG311 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG312;
    TRUE -> STOP;

STATE USEFIRST ARG312 :
    MATCH "" -> GOTO ARG313;
    TRUE -> STOP;

STATE USEFIRST ARG313 :
    MATCH "return methaneLevelCritical;\n" -> GOTO ARG314;
    TRUE -> STOP;

STATE USEFIRST ARG314 :
    MATCH "" -> GOTO ARG315;
    TRUE -> STOP;

STATE USEFIRST ARG315 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG316;
    TRUE -> STOP;

STATE USEFIRST ARG316 :
    MATCH "" -> GOTO ARG317;
    TRUE -> STOP;

STATE USEFIRST ARG317 :
    MATCH "[!__CPAchecker_TMP_0]" -> GOTO ARG318;
    TRUE -> STOP;

STATE USEFIRST ARG318 :
    MATCH "activatePump__wrappee__highWaterSensor();\n" -> GOTO ARG319;
    TRUE -> STOP;

STATE USEFIRST ARG319 :
    MATCH "" -> GOTO ARG320;
    TRUE -> STOP;

STATE USEFIRST ARG320 :
    MATCH "pumpRunning=true;\n" -> GOTO ARG321;
    TRUE -> STOP;

STATE USEFIRST ARG321 :
    MATCH "" -> GOTO ARG322;
    TRUE -> STOP;

STATE USEFIRST ARG322 :
    MATCH "" -> GOTO ARG323;
    TRUE -> STOP;

STATE USEFIRST ARG323 :
    MATCH "" -> GOTO ARG324;
    TRUE -> STOP;

STATE USEFIRST ARG324 :
    MATCH "" -> GOTO ARG325;
    TRUE -> STOP;

STATE USEFIRST ARG325 :
    MATCH "" -> GOTO ARG326;
    TRUE -> STOP;

STATE USEFIRST ARG326 :
    MATCH "processEnvironment__wrappee__base();\n" -> GOTO ARG327;
    TRUE -> STOP;

STATE USEFIRST ARG327 :
    MATCH "" -> GOTO ARG328;
    TRUE -> STOP;

STATE USEFIRST ARG328 :
    MATCH "" -> GOTO ARG329;
    TRUE -> STOP;

STATE USEFIRST ARG329 :
    MATCH "" -> GOTO ARG330;
    TRUE -> STOP;

STATE USEFIRST ARG330 :
    MATCH "" -> GOTO ARG331;
    TRUE -> STOP;

STATE USEFIRST ARG331 :
    MATCH "" -> GOTO ARG332;
    TRUE -> STOP;

STATE USEFIRST ARG332 :
    MATCH "" -> GOTO ARG333;
    TRUE -> STOP;

STATE USEFIRST ARG333 :
    MATCH "" -> GOTO ARG334;
    TRUE -> STOP;

STATE USEFIRST ARG334 :
    MATCH "" -> GOTO ARG335;
    TRUE -> STOP;

STATE USEFIRST ARG335 :
    MATCH "" -> GOTO ARG336;
    TRUE -> STOP;

STATE USEFIRST ARG336 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG337;
    TRUE -> STOP;

STATE USEFIRST ARG337 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG338;
    TRUE -> STOP;

STATE USEFIRST ARG338 :
    MATCH "" -> GOTO ARG339;
    TRUE -> STOP;

STATE USEFIRST ARG339 :
    MATCH "return systemActive;\n" -> GOTO ARG340;
    TRUE -> STOP;

STATE USEFIRST ARG340 :
    MATCH "" -> GOTO ARG341;
    TRUE -> STOP;

STATE USEFIRST ARG341 :
    MATCH "[__CPAchecker_TMP_0]" -> GOTO ARG342;
    TRUE -> STOP;

STATE USEFIRST ARG342 :
    MATCH "Specification1();\n" -> GOTO ARG343;
    TRUE -> STOP;

STATE USEFIRST ARG343 :
    MATCH "" -> GOTO ARG344;
    TRUE -> STOP;

STATE USEFIRST ARG344 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG345;
    TRUE -> STOP;

STATE USEFIRST ARG345 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG346;
    TRUE -> STOP;

STATE USEFIRST ARG346 :
    MATCH "" -> GOTO ARG347;
    TRUE -> STOP;

STATE USEFIRST ARG347 :
    MATCH "return env;\n" -> GOTO ARG348;
    TRUE -> STOP;

STATE USEFIRST ARG348 :
    MATCH "" -> GOTO ARG349;
    TRUE -> STOP;

STATE USEFIRST ARG349 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG350;
    TRUE -> STOP;

STATE USEFIRST ARG350 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG351;
    TRUE -> STOP;

STATE USEFIRST ARG351 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG352;
    TRUE -> STOP;

STATE USEFIRST ARG352 :
    MATCH "" -> GOTO ARG353;
    TRUE -> STOP;

STATE USEFIRST ARG353 :
    MATCH "return methaneLevelCritical;\n" -> GOTO ARG354;
    TRUE -> STOP;

STATE USEFIRST ARG354 :
    MATCH "" -> GOTO ARG355;
    TRUE -> STOP;

STATE USEFIRST ARG355 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG356;
    TRUE -> STOP;

STATE USEFIRST ARG356 :
    MATCH "[!(__CPAchecker_TMP_1)]" -> GOTO ARG357;
    TRUE -> STOP;

STATE USEFIRST ARG357 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG358;
    TRUE -> STOP;

STATE USEFIRST ARG358 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG359;
    TRUE -> STOP;

STATE USEFIRST ARG359 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG360;
    TRUE -> STOP;

STATE USEFIRST ARG360 :
    MATCH "" -> GOTO ARG361;
    TRUE -> STOP;

STATE USEFIRST ARG361 :
    MATCH "return pumpRunning;\n" -> GOTO ARG362;
    TRUE -> STOP;

STATE USEFIRST ARG362 :
    MATCH "" -> GOTO ARG363;
    TRUE -> STOP;

STATE USEFIRST ARG363 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG364;
    TRUE -> STOP;

STATE USEFIRST ARG364 :
    MATCH "[__CPAchecker_TMP_2]" -> GOTO ARG365;
    TRUE -> STOP;

STATE USEFIRST ARG365 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG366;
    TRUE -> STOP;

STATE USEFIRST ARG366 :
    MATCH "[!(b1)]" -> GOTO ARG367;
    TRUE -> STOP;

STATE USEFIRST ARG367 :
    MATCH "" -> GOTO ARG368;
    TRUE -> STOP;

STATE USEFIRST ARG368 :
    MATCH "" -> GOTO ARG369;
    TRUE -> STOP;

STATE USEFIRST ARG369 :
    MATCH "" -> GOTO ARG370;
    TRUE -> STOP;

STATE USEFIRST ARG370 :
    MATCH "" -> GOTO ARG371;
    TRUE -> STOP;

STATE USEFIRST ARG371 :
    MATCH "" -> GOTO ARG372;
    TRUE -> STOP;

STATE USEFIRST ARG372 :
    MATCH "" -> GOTO ARG373;
    TRUE -> STOP;

STATE USEFIRST ARG373 :
    MATCH "[counter < maxLength]" -> GOTO ARG374;
    TRUE -> STOP;

STATE USEFIRST ARG374 :
    MATCH "counter++;\n" -> GOTO ARG375;
    TRUE -> STOP;

STATE USEFIRST ARG375 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG376;
    TRUE -> STOP;

STATE USEFIRST ARG376 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG377;
    TRUE -> STOP;

STATE USEFIRST ARG377 :
    MATCH "" -> GOTO ARG378;
    TRUE -> STOP;

STATE USEFIRST ARG378 :
    MATCH "Random random=new Random();\n" -> GOTO ARG379;
    TRUE -> STOP;

STATE USEFIRST ARG379 :
    MATCH "Random random=new Random();\n" -> GOTO ARG380;
    TRUE -> STOP;

STATE USEFIRST ARG380 :
    MATCH "Random random=new Random();\n" -> GOTO ARG381;
    TRUE -> STOP;

STATE USEFIRST ARG381 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG382;
    TRUE -> STOP;

STATE USEFIRST ARG382 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG383;
    TRUE -> STOP;

STATE USEFIRST ARG383 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG384;
    TRUE -> STOP;

STATE USEFIRST ARG384 :
    MATCH "" -> GOTO ARG385;
    TRUE -> STOP;

STATE USEFIRST ARG385 :
    MATCH "boolean action1=getBoolean();\n" -> GOTO ARG386;
    TRUE -> STOP;

STATE USEFIRST ARG386 :
    MATCH "[__CPAchecker_TMP_1]" -> GOTO ARG388;
    TRUE -> STOP;

STATE USEFIRST ARG388 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG389;
    TRUE -> STOP;

STATE USEFIRST ARG389 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG390;
    TRUE -> STOP;

STATE USEFIRST ARG390 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG391;
    TRUE -> STOP;

STATE USEFIRST ARG391 :
    MATCH "" -> GOTO ARG392;
    TRUE -> STOP;

STATE USEFIRST ARG392 :
    MATCH "Random random=new Random();\n" -> GOTO ARG393;
    TRUE -> STOP;

STATE USEFIRST ARG393 :
    MATCH "Random random=new Random();\n" -> GOTO ARG394;
    TRUE -> STOP;

STATE USEFIRST ARG394 :
    MATCH "Random random=new Random();\n" -> GOTO ARG395;
    TRUE -> STOP;

STATE USEFIRST ARG395 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG396;
    TRUE -> STOP;

STATE USEFIRST ARG396 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG397;
    TRUE -> STOP;

STATE USEFIRST ARG397 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG398;
    TRUE -> STOP;

STATE USEFIRST ARG398 :
    MATCH "" -> GOTO ARG399;
    TRUE -> STOP;

STATE USEFIRST ARG399 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG400;
    TRUE -> STOP;

STATE USEFIRST ARG400 :
    MATCH "[__CPAchecker_TMP_2]" -> GOTO ARG402;
    TRUE -> STOP;

STATE USEFIRST ARG402 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG403;
    TRUE -> STOP;

STATE USEFIRST ARG403 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG404;
    TRUE -> STOP;

STATE USEFIRST ARG404 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG405;
    TRUE -> STOP;

STATE USEFIRST ARG405 :
    MATCH "" -> GOTO ARG406;
    TRUE -> STOP;

STATE USEFIRST ARG406 :
    MATCH "Random random=new Random();\n" -> GOTO ARG407;
    TRUE -> STOP;

STATE USEFIRST ARG407 :
    MATCH "Random random=new Random();\n" -> GOTO ARG408;
    TRUE -> STOP;

STATE USEFIRST ARG408 :
    MATCH "Random random=new Random();\n" -> GOTO ARG409;
    TRUE -> STOP;

STATE USEFIRST ARG409 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG410;
    TRUE -> STOP;

STATE USEFIRST ARG410 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG411;
    TRUE -> STOP;

STATE USEFIRST ARG411 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG412;
    TRUE -> STOP;

STATE USEFIRST ARG412 :
    MATCH "" -> GOTO ARG413;
    TRUE -> STOP;

STATE USEFIRST ARG413 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG414;
    TRUE -> STOP;

STATE USEFIRST ARG414 :
    MATCH "[__CPAchecker_TMP_3]" -> GOTO ARG416;
    TRUE -> STOP;

STATE USEFIRST ARG416 :
    MATCH "__CPAchecker_TMP_3" -> GOTO ARG417;
    TRUE -> STOP;

STATE USEFIRST ARG417 :
    MATCH "boolean action4=false;\n" -> GOTO ARG418;
    TRUE -> STOP;

STATE USEFIRST ARG418 :
    MATCH "[!(!action3)]" -> GOTO ARG419;
    TRUE -> STOP;

STATE USEFIRST ARG419 :
    MATCH "[action1]" -> GOTO ARG420;
    TRUE -> STOP;

STATE USEFIRST ARG420 :
    MATCH "a.waterRise();\n" -> GOTO ARG421;
    TRUE -> STOP;

STATE USEFIRST ARG421 :
    MATCH "" -> GOTO ARG422;
    TRUE -> STOP;

STATE USEFIRST ARG422 :
    MATCH "env.waterRise();\n" -> GOTO ARG423;
    TRUE -> STOP;

STATE USEFIRST ARG423 :
    MATCH "" -> GOTO ARG424;
    TRUE -> STOP;

STATE USEFIRST ARG424 :
    MATCH "switch (waterLevel)" -> GOTO ARG425;
    TRUE -> STOP;

STATE USEFIRST ARG425 :
    MATCH "[!(MinePumpSystem.Environment_waterLevel == 0)]" -> GOTO ARG426;
    TRUE -> STOP;

STATE USEFIRST ARG426 :
    MATCH "[!(MinePumpSystem.Environment_waterLevel == 1)]" -> GOTO ARG427;
    TRUE -> STOP;

STATE USEFIRST ARG427 :
    MATCH "" -> GOTO ARG428;
    TRUE -> STOP;

STATE USEFIRST ARG428 :
    MATCH "" -> GOTO ARG429;
    TRUE -> STOP;

STATE USEFIRST ARG429 :
    MATCH "" -> GOTO ARG430;
    TRUE -> STOP;

STATE USEFIRST ARG430 :
    MATCH "" -> GOTO ARG431;
    TRUE -> STOP;

STATE USEFIRST ARG431 :
    MATCH "" -> GOTO ARG432;
    TRUE -> STOP;

STATE USEFIRST ARG432 :
    MATCH "" -> GOTO ARG433;
    TRUE -> STOP;

STATE USEFIRST ARG433 :
    MATCH "[action2]" -> GOTO ARG434;
    TRUE -> STOP;

STATE USEFIRST ARG434 :
    MATCH "a.methaneChange();\n" -> GOTO ARG435;
    TRUE -> STOP;

STATE USEFIRST ARG435 :
    MATCH "" -> GOTO ARG436;
    TRUE -> STOP;

STATE USEFIRST ARG436 :
    MATCH "env.changeMethaneLevel();\n" -> GOTO ARG437;
    TRUE -> STOP;

STATE USEFIRST ARG437 :
    MATCH "" -> GOTO ARG438;
    TRUE -> STOP;

STATE USEFIRST ARG438 :
    MATCH "[!MinePumpSystem.Environment_methaneLevelCritical]" -> GOTO ARG439;
    TRUE -> STOP;

STATE USEFIRST ARG439 :
    MATCH "!MinePumpSystem.Environment_methaneLevelCritical" -> GOTO ARG440;
    TRUE -> STOP;

STATE USEFIRST ARG440 :
    MATCH "" -> GOTO ARG441;
    TRUE -> STOP;

STATE USEFIRST ARG441 :
    MATCH "" -> GOTO ARG442;
    TRUE -> STOP;

STATE USEFIRST ARG442 :
    MATCH "" -> GOTO ARG443;
    TRUE -> STOP;

STATE USEFIRST ARG443 :
    MATCH "" -> GOTO ARG444;
    TRUE -> STOP;

STATE USEFIRST ARG444 :
    MATCH "" -> GOTO ARG445;
    TRUE -> STOP;

STATE USEFIRST ARG445 :
    MATCH "[action3]" -> GOTO ARG446;
    TRUE -> STOP;

STATE USEFIRST ARG446 :
    MATCH "a.startSystem();\n" -> GOTO ARG447;
    TRUE -> STOP;

STATE USEFIRST ARG447 :
    MATCH "" -> GOTO ARG448;
    TRUE -> STOP;

STATE USEFIRST ARG448 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG449;
    TRUE -> STOP;

STATE USEFIRST ARG449 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG450;
    TRUE -> STOP;

STATE USEFIRST ARG450 :
    MATCH "" -> GOTO ARG451;
    TRUE -> STOP;

STATE USEFIRST ARG451 :
    MATCH "return systemActive;\n" -> GOTO ARG452;
    TRUE -> STOP;

STATE USEFIRST ARG452 :
    MATCH "" -> GOTO ARG453;
    TRUE -> STOP;

STATE USEFIRST ARG453 :
    MATCH "[!(!__CPAchecker_TMP_0)]" -> GOTO ARG454;
    TRUE -> STOP;

STATE USEFIRST ARG454 :
    MATCH "" -> GOTO ARG455;
    TRUE -> STOP;

STATE USEFIRST ARG455 :
    MATCH "" -> GOTO ARG456;
    TRUE -> STOP;

STATE USEFIRST ARG456 :
    MATCH "" -> GOTO ARG457;
    TRUE -> STOP;

STATE USEFIRST ARG457 :
    MATCH "a.timeShift();\n" -> GOTO ARG458;
    TRUE -> STOP;

STATE USEFIRST ARG458 :
    MATCH "" -> GOTO ARG459;
    TRUE -> STOP;

STATE USEFIRST ARG459 :
    MATCH "p.timeShift();\n" -> GOTO ARG460;
    TRUE -> STOP;

STATE USEFIRST ARG460 :
    MATCH "" -> GOTO ARG461;
    TRUE -> STOP;

STATE USEFIRST ARG461 :
    MATCH "[MinePumpSystem.MinePump_pumpRunning]" -> GOTO ARG462;
    TRUE -> STOP;

STATE USEFIRST ARG462 :
    MATCH "env.lowerWaterLevel();\n" -> GOTO ARG463;
    TRUE -> STOP;

STATE USEFIRST ARG463 :
    MATCH "" -> GOTO ARG464;
    TRUE -> STOP;

STATE USEFIRST ARG464 :
    MATCH "switch (waterLevel)" -> GOTO ARG465;
    TRUE -> STOP;

STATE USEFIRST ARG465 :
    MATCH "[MinePumpSystem.Environment_waterLevel == 2]" -> GOTO ARG466;
    TRUE -> STOP;

STATE USEFIRST ARG466 :
    MATCH "waterLevel=1;\n" -> GOTO ARG467;
    TRUE -> STOP;

STATE USEFIRST ARG467 :
    MATCH "break;\n" -> GOTO ARG468;
    TRUE -> STOP;

STATE USEFIRST ARG468 :
    MATCH "" -> GOTO ARG469;
    TRUE -> STOP;

STATE USEFIRST ARG469 :
    MATCH "" -> GOTO ARG470;
    TRUE -> STOP;

STATE USEFIRST ARG470 :
    MATCH "" -> GOTO ARG471;
    TRUE -> STOP;

STATE USEFIRST ARG471 :
    MATCH "[MinePumpSystem.MinePump_systemActive]" -> GOTO ARG472;
    TRUE -> STOP;

STATE USEFIRST ARG472 :
    MATCH "processEnvironment();\n" -> GOTO ARG473;
    TRUE -> STOP;

STATE USEFIRST ARG473 :
    MATCH "" -> GOTO ARG474;
    TRUE -> STOP;

STATE USEFIRST ARG474 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG475;
    TRUE -> STOP;

STATE USEFIRST ARG475 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG476;
    TRUE -> STOP;

STATE USEFIRST ARG476 :
    MATCH "" -> GOTO ARG477;
    TRUE -> STOP;

STATE USEFIRST ARG477 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG478;
    TRUE -> STOP;

STATE USEFIRST ARG478 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG479;
    TRUE -> STOP;

STATE USEFIRST ARG479 :
    MATCH "" -> GOTO ARG480;
    TRUE -> STOP;

STATE USEFIRST ARG480 :
    MATCH "return waterLevel != 2;\n" -> GOTO ARG481;
    TRUE -> STOP;

STATE USEFIRST ARG481 :
    MATCH "" -> GOTO ARG482;
    TRUE -> STOP;

STATE USEFIRST ARG482 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG483;
    TRUE -> STOP;

STATE USEFIRST ARG483 :
    MATCH "" -> GOTO ARG484;
    TRUE -> STOP;

STATE USEFIRST ARG484 :
    MATCH "[!(!MinePumpSystem.MinePump_pumpRunning)]" -> GOTO ARG485;
    TRUE -> STOP;

STATE USEFIRST ARG485 :
    MATCH "processEnvironment__wrappee__base();\n" -> GOTO ARG486;
    TRUE -> STOP;

STATE USEFIRST ARG486 :
    MATCH "" -> GOTO ARG487;
    TRUE -> STOP;

STATE USEFIRST ARG487 :
    MATCH "" -> GOTO ARG488;
    TRUE -> STOP;

STATE USEFIRST ARG488 :
    MATCH "" -> GOTO ARG489;
    TRUE -> STOP;

STATE USEFIRST ARG489 :
    MATCH "" -> GOTO ARG490;
    TRUE -> STOP;

STATE USEFIRST ARG490 :
    MATCH "" -> GOTO ARG491;
    TRUE -> STOP;

STATE USEFIRST ARG491 :
    MATCH "" -> GOTO ARG492;
    TRUE -> STOP;

STATE USEFIRST ARG492 :
    MATCH "" -> GOTO ARG493;
    TRUE -> STOP;

STATE USEFIRST ARG493 :
    MATCH "" -> GOTO ARG494;
    TRUE -> STOP;

STATE USEFIRST ARG494 :
    MATCH "" -> GOTO ARG495;
    TRUE -> STOP;

STATE USEFIRST ARG495 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG496;
    TRUE -> STOP;

STATE USEFIRST ARG496 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG497;
    TRUE -> STOP;

STATE USEFIRST ARG497 :
    MATCH "" -> GOTO ARG498;
    TRUE -> STOP;

STATE USEFIRST ARG498 :
    MATCH "return systemActive;\n" -> GOTO ARG499;
    TRUE -> STOP;

STATE USEFIRST ARG499 :
    MATCH "" -> GOTO ARG500;
    TRUE -> STOP;

STATE USEFIRST ARG500 :
    MATCH "[__CPAchecker_TMP_0]" -> GOTO ARG501;
    TRUE -> STOP;

STATE USEFIRST ARG501 :
    MATCH "Specification1();\n" -> GOTO ARG502;
    TRUE -> STOP;

STATE USEFIRST ARG502 :
    MATCH "" -> GOTO ARG503;
    TRUE -> STOP;

STATE USEFIRST ARG503 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG504;
    TRUE -> STOP;

STATE USEFIRST ARG504 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG505;
    TRUE -> STOP;

STATE USEFIRST ARG505 :
    MATCH "" -> GOTO ARG506;
    TRUE -> STOP;

STATE USEFIRST ARG506 :
    MATCH "return env;\n" -> GOTO ARG507;
    TRUE -> STOP;

STATE USEFIRST ARG507 :
    MATCH "" -> GOTO ARG508;
    TRUE -> STOP;

STATE USEFIRST ARG508 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG509;
    TRUE -> STOP;

STATE USEFIRST ARG509 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG510;
    TRUE -> STOP;

STATE USEFIRST ARG510 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG511;
    TRUE -> STOP;

STATE USEFIRST ARG511 :
    MATCH "" -> GOTO ARG512;
    TRUE -> STOP;

STATE USEFIRST ARG512 :
    MATCH "return methaneLevelCritical;\n" -> GOTO ARG513;
    TRUE -> STOP;

STATE USEFIRST ARG513 :
    MATCH "" -> GOTO ARG514;
    TRUE -> STOP;

STATE USEFIRST ARG514 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG515;
    TRUE -> STOP;

STATE USEFIRST ARG515 :
    MATCH "[__CPAchecker_TMP_1]" -> GOTO ARG516;
    TRUE -> STOP;

STATE USEFIRST ARG516 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG517;
    TRUE -> STOP;

STATE USEFIRST ARG517 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG518;
    TRUE -> STOP;

STATE USEFIRST ARG518 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG519;
    TRUE -> STOP;

STATE USEFIRST ARG519 :
    MATCH "" -> GOTO ARG520;
    TRUE -> STOP;

STATE USEFIRST ARG520 :
    MATCH "return pumpRunning;\n" -> GOTO ARG521;
    TRUE -> STOP;

STATE USEFIRST ARG521 :
    MATCH "" -> GOTO ARG522;
    TRUE -> STOP;

STATE USEFIRST ARG522 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG523;
    TRUE -> STOP;

STATE USEFIRST ARG523 :
    MATCH "[__CPAchecker_TMP_2]" -> GOTO ARG524;
    TRUE -> STOP;

STATE USEFIRST ARG524 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG525;
    TRUE -> STOP;

STATE USEFIRST ARG525 :
    MATCH "[b1]" -> GOTO ARG526;
    TRUE -> STOP;

STATE USEFIRST ARG526 :
    MATCH "[b2]" -> GOTO ARG527;
    TRUE -> STOP;

STATE USEFIRST ARG527 :
    MATCH "false" -> GOTO ARG528;
    TRUE -> STOP;

STATE USEFIRST ARG528 :
    MATCH "assert false;\n" -> ERROR;
    TRUE -> STOP;

STATE USEFIRST ARG529 :
    TRUE -> STOP;

END AUTOMATON
