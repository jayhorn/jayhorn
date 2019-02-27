package jayhorn.solver.princess;

import jayhorn.solver.StringADTFactory;
import jayhorn.solver.ProverType;
import jayhorn.solver.IntType;
import jayhorn.solver.ProverADT;
import jayhorn.solver.ADTTempType;

public class PrincessStringADTFactory implements StringADTFactory {

    private final ProverType pt = IntType.INSTANCE;
    private final int ti = ADTTempType.ListADTTypeIndex;

    public ProverADT spawnStringADT() {
        return PrincessADT.mkADT(new String[]{"List[" + pt.toString() + "]"},
                new String[]{"nil", "cons"},
                new int[]{ADTTempType.ListADTTypeIndex, ti},
                new ProverType[][]{{}, {pt, ADTTempType.getADTTempType(ti)}},
                new String[][]{{}, {"head", "tail"}});
    }

}
