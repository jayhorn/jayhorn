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
    MATCH " MinePumpSystem.Environment Actions_env;" -> GOTO ARG9;
    TRUE -> STOP;

STATE USEFIRST ARG9 :
    MATCH " boolean Actions_methAndRunningLastTime = false;" -> GOTO ARG10;
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
    MATCH "private MinePumpSystem.Environment.WaterLevelEnum MinePumpSystem.Environment_waterLevel = MinePumpSystem.Environment.WaterLevelEnum_normal;" -> GOTO ARG15;
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
    MATCH "[!(__CPAchecker_TMP_1)]" -> GOTO ARG46;
    TRUE -> STOP;

STATE USEFIRST ARG46 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG150;
    TRUE -> STOP;

STATE USEFIRST ARG150 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG151;
    TRUE -> STOP;

STATE USEFIRST ARG151 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG152;
    TRUE -> STOP;

STATE USEFIRST ARG152 :
    MATCH "" -> GOTO ARG153;
    TRUE -> STOP;

STATE USEFIRST ARG153 :
    MATCH "Random random=new Random();\n" -> GOTO ARG154;
    TRUE -> STOP;

STATE USEFIRST ARG154 :
    MATCH "Random random=new Random();\n" -> GOTO ARG155;
    TRUE -> STOP;

STATE USEFIRST ARG155 :
    MATCH "Random random=new Random();\n" -> GOTO ARG156;
    TRUE -> STOP;

STATE USEFIRST ARG156 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG157;
    TRUE -> STOP;

STATE USEFIRST ARG157 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG158;
    TRUE -> STOP;

STATE USEFIRST ARG158 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG159;
    TRUE -> STOP;

STATE USEFIRST ARG159 :
    MATCH "" -> GOTO ARG160;
    TRUE -> STOP;

STATE USEFIRST ARG160 :
    MATCH "boolean action2=getBoolean();\n" -> GOTO ARG161;
    TRUE -> STOP;

STATE USEFIRST ARG161 :
    MATCH "[__CPAchecker_TMP_2]" -> GOTO ARG163;
    TRUE -> STOP;

STATE USEFIRST ARG163 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG164;
    TRUE -> STOP;

STATE USEFIRST ARG164 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG165;
    TRUE -> STOP;

STATE USEFIRST ARG165 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG166;
    TRUE -> STOP;

STATE USEFIRST ARG166 :
    MATCH "" -> GOTO ARG167;
    TRUE -> STOP;

STATE USEFIRST ARG167 :
    MATCH "Random random=new Random();\n" -> GOTO ARG168;
    TRUE -> STOP;

STATE USEFIRST ARG168 :
    MATCH "Random random=new Random();\n" -> GOTO ARG169;
    TRUE -> STOP;

STATE USEFIRST ARG169 :
    MATCH "Random random=new Random();\n" -> GOTO ARG170;
    TRUE -> STOP;

STATE USEFIRST ARG170 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG171;
    TRUE -> STOP;

STATE USEFIRST ARG171 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG172;
    TRUE -> STOP;

STATE USEFIRST ARG172 :
    MATCH "return random.nextBoolean();\n" -> GOTO ARG173;
    TRUE -> STOP;

STATE USEFIRST ARG173 :
    MATCH "" -> GOTO ARG174;
    TRUE -> STOP;

STATE USEFIRST ARG174 :
    MATCH "boolean action3=getBoolean();\n" -> GOTO ARG175;
    TRUE -> STOP;

STATE USEFIRST ARG175 :
    MATCH "[__CPAchecker_TMP_3]" -> GOTO ARG177;
    TRUE -> STOP;

STATE USEFIRST ARG177 :
    MATCH "__CPAchecker_TMP_3" -> GOTO ARG178;
    TRUE -> STOP;

STATE USEFIRST ARG178 :
    MATCH "boolean action4=false;\n" -> GOTO ARG179;
    TRUE -> STOP;

STATE USEFIRST ARG179 :
    MATCH "[!(!action3)]" -> GOTO ARG180;
    TRUE -> STOP;

STATE USEFIRST ARG180 :
    MATCH "[!(action1)]" -> GOTO ARG181;
    TRUE -> STOP;

STATE USEFIRST ARG181 :
    MATCH "[action2]" -> GOTO ARG182;
    TRUE -> STOP;

STATE USEFIRST ARG182 :
    MATCH "a.methaneChange();\n" -> GOTO ARG183;
    TRUE -> STOP;

STATE USEFIRST ARG183 :
    MATCH "" -> GOTO ARG184;
    TRUE -> STOP;

STATE USEFIRST ARG184 :
    MATCH "env.changeMethaneLevel();\n" -> GOTO ARG185;
    TRUE -> STOP;

STATE USEFIRST ARG185 :
    MATCH "" -> GOTO ARG186;
    TRUE -> STOP;

STATE USEFIRST ARG186 :
    MATCH "[!MinePumpSystem.Environment_methaneLevelCritical]" -> GOTO ARG187;
    TRUE -> STOP;

STATE USEFIRST ARG187 :
    MATCH "!MinePumpSystem.Environment_methaneLevelCritical" -> GOTO ARG188;
    TRUE -> STOP;

STATE USEFIRST ARG188 :
    MATCH "" -> GOTO ARG189;
    TRUE -> STOP;

STATE USEFIRST ARG189 :
    MATCH "" -> GOTO ARG190;
    TRUE -> STOP;

STATE USEFIRST ARG190 :
    MATCH "" -> GOTO ARG191;
    TRUE -> STOP;

STATE USEFIRST ARG191 :
    MATCH "" -> GOTO ARG192;
    TRUE -> STOP;

STATE USEFIRST ARG192 :
    MATCH "" -> GOTO ARG193;
    TRUE -> STOP;

STATE USEFIRST ARG193 :
    MATCH "[action3]" -> GOTO ARG194;
    TRUE -> STOP;

STATE USEFIRST ARG194 :
    MATCH "a.startSystem();\n" -> GOTO ARG195;
    TRUE -> STOP;

STATE USEFIRST ARG195 :
    MATCH "" -> GOTO ARG196;
    TRUE -> STOP;

STATE USEFIRST ARG196 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG197;
    TRUE -> STOP;

STATE USEFIRST ARG197 :
    MATCH "!__CPAchecker_TMP_0" -> GOTO ARG198;
    TRUE -> STOP;

STATE USEFIRST ARG198 :
    MATCH "" -> GOTO ARG199;
    TRUE -> STOP;

STATE USEFIRST ARG199 :
    MATCH "return systemActive;\n" -> GOTO ARG200;
    TRUE -> STOP;

STATE USEFIRST ARG200 :
    MATCH "" -> GOTO ARG201;
    TRUE -> STOP;

STATE USEFIRST ARG201 :
    MATCH "[!(!__CPAchecker_TMP_0)]" -> GOTO ARG202;
    TRUE -> STOP;

STATE USEFIRST ARG202 :
    MATCH "" -> GOTO ARG203;
    TRUE -> STOP;

STATE USEFIRST ARG203 :
    MATCH "" -> GOTO ARG204;
    TRUE -> STOP;

STATE USEFIRST ARG204 :
    MATCH "" -> GOTO ARG205;
    TRUE -> STOP;

STATE USEFIRST ARG205 :
    MATCH "a.timeShift();\n" -> GOTO ARG206;
    TRUE -> STOP;

STATE USEFIRST ARG206 :
    MATCH "" -> GOTO ARG207;
    TRUE -> STOP;

STATE USEFIRST ARG207 :
    MATCH "p.timeShift();\n" -> GOTO ARG208;
    TRUE -> STOP;

STATE USEFIRST ARG208 :
    MATCH "" -> GOTO ARG209;
    TRUE -> STOP;

STATE USEFIRST ARG209 :
    MATCH "[!(MinePumpSystem.MinePump_pumpRunning)]" -> GOTO ARG210;
    TRUE -> STOP;

STATE USEFIRST ARG210 :
    MATCH "[MinePumpSystem.MinePump_systemActive]" -> GOTO ARG211;
    TRUE -> STOP;

STATE USEFIRST ARG211 :
    MATCH "processEnvironment();\n" -> GOTO ARG212;
    TRUE -> STOP;

STATE USEFIRST ARG212 :
    MATCH "" -> GOTO ARG213;
    TRUE -> STOP;

STATE USEFIRST ARG213 :
    MATCH "MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG214;
    TRUE -> STOP;

STATE USEFIRST ARG214 :
    MATCH "MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG215;
    TRUE -> STOP;

STATE USEFIRST ARG215 :
    MATCH "" -> GOTO ARG216;
    TRUE -> STOP;

STATE USEFIRST ARG216 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG217;
    TRUE -> STOP;

STATE USEFIRST ARG217 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG218;
    TRUE -> STOP;

STATE USEFIRST ARG218 :
    MATCH "" -> GOTO ARG219;
    TRUE -> STOP;

STATE USEFIRST ARG219 :
    MATCH "return methaneLevelCritical;\n" -> GOTO ARG220;
    TRUE -> STOP;

STATE USEFIRST ARG220 :
    MATCH "" -> GOTO ARG221;
    TRUE -> STOP;

STATE USEFIRST ARG221 :
    MATCH "return env.isMethaneLevelCritical();\n" -> GOTO ARG222;
    TRUE -> STOP;

STATE USEFIRST ARG222 :
    MATCH "" -> GOTO ARG223;
    TRUE -> STOP;

STATE USEFIRST ARG223 :
    MATCH "[!(MinePumpSystem.MinePump_pumpRunning)]" -> GOTO ARG224;
    TRUE -> STOP;

STATE USEFIRST ARG224 :
    MATCH "processEnvironment__wrappee__methaneQuery();\n" -> GOTO ARG225;
    TRUE -> STOP;

STATE USEFIRST ARG225 :
    MATCH "" -> GOTO ARG226;
    TRUE -> STOP;

STATE USEFIRST ARG226 :
    MATCH "MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG227;
    TRUE -> STOP;

STATE USEFIRST ARG227 :
    MATCH "MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG228;
    TRUE -> STOP;

STATE USEFIRST ARG228 :
    MATCH "" -> GOTO ARG229;
    TRUE -> STOP;

STATE USEFIRST ARG229 :
    MATCH "return !env.isLowWaterSensorDry();\n" -> GOTO ARG230;
    TRUE -> STOP;

STATE USEFIRST ARG230 :
    MATCH "return !env.isLowWaterSensorDry();\n" -> GOTO ARG231;
    TRUE -> STOP;

STATE USEFIRST ARG231 :
    MATCH "" -> GOTO ARG232;
    TRUE -> STOP;

STATE USEFIRST ARG232 :
    MATCH "return waterLevel == WaterLevelEnum.low;\n" -> GOTO ARG233;
    TRUE -> STOP;

STATE USEFIRST ARG233 :
    MATCH "" -> GOTO ARG234;
    TRUE -> STOP;

STATE USEFIRST ARG234 :
    MATCH "return !env.isLowWaterSensorDry();\n" -> GOTO ARG235;
    TRUE -> STOP;

STATE USEFIRST ARG235 :
    MATCH "" -> GOTO ARG236;
    TRUE -> STOP;

STATE USEFIRST ARG236 :
    MATCH "[!(MinePumpSystem.MinePump_pumpRunning)]" -> GOTO ARG237;
    TRUE -> STOP;

STATE USEFIRST ARG237 :
    MATCH "processEnvironment__wrappee__highWaterSensor();\n" -> GOTO ARG238;
    TRUE -> STOP;

STATE USEFIRST ARG238 :
    MATCH "" -> GOTO ARG239;
    TRUE -> STOP;

STATE USEFIRST ARG239 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG240;
    TRUE -> STOP;

STATE USEFIRST ARG240 :
    MATCH "!MinePumpSystem.MinePump_pumpRunning" -> GOTO ARG241;
    TRUE -> STOP;

STATE USEFIRST ARG241 :
    MATCH "" -> GOTO ARG242;
    TRUE -> STOP;

STATE USEFIRST ARG242 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG243;
    TRUE -> STOP;

STATE USEFIRST ARG243 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG244;
    TRUE -> STOP;

STATE USEFIRST ARG244 :
    MATCH "" -> GOTO ARG245;
    TRUE -> STOP;

STATE USEFIRST ARG245 :
    MATCH "return waterLevel != WaterLevelEnum.high;\n" -> GOTO ARG246;
    TRUE -> STOP;

STATE USEFIRST ARG246 :
    MATCH "" -> GOTO ARG247;
    TRUE -> STOP;

STATE USEFIRST ARG247 :
    MATCH "return !env.isHighWaterSensorDry();\n" -> GOTO ARG248;
    TRUE -> STOP;

STATE USEFIRST ARG248 :
    MATCH "" -> GOTO ARG249;
    TRUE -> STOP;

STATE USEFIRST ARG249 :
    MATCH "[!MinePumpSystem.MinePump_pumpRunning]" -> GOTO ARG250;
    TRUE -> STOP;

STATE USEFIRST ARG250 :
    MATCH "[!(__CPAchecker_TMP_0)]" -> GOTO ARG251;
    TRUE -> STOP;

STATE USEFIRST ARG251 :
    MATCH "processEnvironment__wrappee__base();\n" -> GOTO ARG252;
    TRUE -> STOP;

STATE USEFIRST ARG252 :
    MATCH "" -> GOTO ARG253;
    TRUE -> STOP;

STATE USEFIRST ARG253 :
    MATCH "" -> GOTO ARG254;
    TRUE -> STOP;

STATE USEFIRST ARG254 :
    MATCH "" -> GOTO ARG255;
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
    MATCH "" -> GOTO ARG262;
    TRUE -> STOP;

STATE USEFIRST ARG262 :
    MATCH "" -> GOTO ARG263;
    TRUE -> STOP;

STATE USEFIRST ARG263 :
    MATCH "" -> GOTO ARG264;
    TRUE -> STOP;

STATE USEFIRST ARG264 :
    MATCH "" -> GOTO ARG265;
    TRUE -> STOP;

STATE USEFIRST ARG265 :
    MATCH "" -> GOTO ARG266;
    TRUE -> STOP;

STATE USEFIRST ARG266 :
    MATCH "" -> GOTO ARG267;
    TRUE -> STOP;

STATE USEFIRST ARG267 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG268;
    TRUE -> STOP;

STATE USEFIRST ARG268 :
    MATCH "__CPAchecker_TMP_0" -> GOTO ARG269;
    TRUE -> STOP;

STATE USEFIRST ARG269 :
    MATCH "" -> GOTO ARG270;
    TRUE -> STOP;

STATE USEFIRST ARG270 :
    MATCH "return systemActive;\n" -> GOTO ARG271;
    TRUE -> STOP;

STATE USEFIRST ARG271 :
    MATCH "" -> GOTO ARG272;
    TRUE -> STOP;

STATE USEFIRST ARG272 :
    MATCH "[__CPAchecker_TMP_0]" -> GOTO ARG273;
    TRUE -> STOP;

STATE USEFIRST ARG273 :
    MATCH "Specification1();\n" -> GOTO ARG274;
    TRUE -> STOP;

STATE USEFIRST ARG274 :
    MATCH "" -> GOTO ARG275;
    TRUE -> STOP;

STATE USEFIRST ARG275 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG276;
    TRUE -> STOP;

STATE USEFIRST ARG276 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG277;
    TRUE -> STOP;

STATE USEFIRST ARG277 :
    MATCH "" -> GOTO ARG278;
    TRUE -> STOP;

STATE USEFIRST ARG278 :
    MATCH "return env;\n" -> GOTO ARG279;
    TRUE -> STOP;

STATE USEFIRST ARG279 :
    MATCH "" -> GOTO ARG280;
    TRUE -> STOP;

STATE USEFIRST ARG280 :
    MATCH "Environment e=p.getEnv();\n" -> GOTO ARG281;
    TRUE -> STOP;

STATE USEFIRST ARG281 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG282;
    TRUE -> STOP;

STATE USEFIRST ARG282 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG283;
    TRUE -> STOP;

STATE USEFIRST ARG283 :
    MATCH "" -> GOTO ARG284;
    TRUE -> STOP;

STATE USEFIRST ARG284 :
    MATCH "return methaneLevelCritical;\n" -> GOTO ARG285;
    TRUE -> STOP;

STATE USEFIRST ARG285 :
    MATCH "" -> GOTO ARG286;
    TRUE -> STOP;

STATE USEFIRST ARG286 :
    MATCH "boolean b1=e.isMethaneLevelCritical();\n" -> GOTO ARG287;
    TRUE -> STOP;

STATE USEFIRST ARG287 :
    MATCH "[__CPAchecker_TMP_1]" -> GOTO ARG288;
    TRUE -> STOP;

STATE USEFIRST ARG288 :
    MATCH "__CPAchecker_TMP_1" -> GOTO ARG289;
    TRUE -> STOP;

STATE USEFIRST ARG289 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG290;
    TRUE -> STOP;

STATE USEFIRST ARG290 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG291;
    TRUE -> STOP;

STATE USEFIRST ARG291 :
    MATCH "" -> GOTO ARG292;
    TRUE -> STOP;

STATE USEFIRST ARG292 :
    MATCH "return pumpRunning;\n" -> GOTO ARG293;
    TRUE -> STOP;

STATE USEFIRST ARG293 :
    MATCH "" -> GOTO ARG294;
    TRUE -> STOP;

STATE USEFIRST ARG294 :
    MATCH "boolean b2=p.isPumpRunning();\n" -> GOTO ARG295;
    TRUE -> STOP;

STATE USEFIRST ARG295 :
    MATCH "[!(__CPAchecker_TMP_2)]" -> GOTO ARG296;
    TRUE -> STOP;

STATE USEFIRST ARG296 :
    MATCH "__CPAchecker_TMP_2" -> GOTO ARG297;
    TRUE -> STOP;

STATE USEFIRST ARG297 :
    MATCH "System.out.println(\"b1 \" + b1);\n" -> GOTO ARG298;
    TRUE -> STOP;

STATE USEFIRST ARG298 :
    MATCH "System.out.println(\"b2 \" + b2);\n" -> GOTO ARG299;
    TRUE -> STOP;

STATE USEFIRST ARG299 :
    MATCH "false" -> GOTO ARG300;
    TRUE -> STOP;

STATE USEFIRST ARG300 :
    MATCH "assert false;\n" -> ERROR;
    TRUE -> STOP;

STATE USEFIRST ARG301 :
    TRUE -> STOP;

END AUTOMATON
