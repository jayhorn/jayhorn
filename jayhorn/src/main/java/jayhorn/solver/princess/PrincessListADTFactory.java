package jayhorn.solver.princess;

import jayhorn.solver.ADTTempType;
import jayhorn.solver.ListADTFactory;
import jayhorn.solver.ProverADT;
import jayhorn.solver.ProverType;

public class PrincessListADTFactory implements ListADTFactory {

    private ProverType pt;

    public PrincessListADTFactory(ProverType proverType) {
        this.pt = proverType;
    }

    public ProverADT mkListADT() {
        return PrincessADT.mkADT(new String[]{"List[" + pt.toString() + "]"},
                new String[]{"nil", "cons"},
                new int[]{ADTTempType.ListADTTypeIndex, ADTTempType.ListADTTypeIndex},
                new ProverType[][]{{}, {pt, ADTTempType.getADTTempType(ADTTempType.ListADTTypeIndex)}},
                new String[][]{{}, {"head", "tail"}});
    }

}
