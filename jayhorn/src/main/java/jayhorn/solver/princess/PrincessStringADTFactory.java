package jayhorn.solver.princess;

import jayhorn.solver.StringADTFactory;
import jayhorn.solver.ProverType;
import jayhorn.solver.IntType;
import jayhorn.solver.ProverADT;
import jayhorn.solver.ADTTempType;

public class PrincessStringADTFactory implements StringADTFactory {

    private final ProverType proverType = IntType.INSTANCE;
    private static final int listADTTypeIndex = ADTTempType.ListADTTypeIndex;

    public ProverADT spawnStringADT() {
        return PrincessADT.mkADT(new String[]{"List[" + proverType.toString() + "]"},
                new String[]{"nil", "cons"},
                new int[]{ADTTempType.ListADTTypeIndex, listADTTypeIndex},
                new ProverType[][]{{}, {proverType, ADTTempType.getADTTempType(listADTTypeIndex)}},
                new String[][]{{}, {"head", "tail"}});
    }

}
