package org.sybila.parasim.model.verification.stlstar;

import java.util.HashSet;
import java.util.Set;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.TemporalFormula;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestStarDownShaker {

    private boolean testEquality(Formula phi, Formula psi, Set<Integer> phiIndices, Set<Integer> psiIndices) {
        if (phi.getArity() != psi.getArity()) {
            return false;
        }
        if (phi.getType() != psi.getType()) {
            return false;
        }
        switch (phi.getType()) {
            case FREEZE:
                phiIndices.add(((FreezeFormula) phi).getFreezeIndex());
                psiIndices.add(((FreezeFormula) psi).getFreezeIndex());
                break;
            case UNTIL:
            case GLOBALLY:
            case FUTURE:
                if (!((TemporalFormula) phi).getInterval().equals(((TemporalFormula) psi).getInterval())) {
                    return false;
                }
                if (!phiIndices.equals(psiIndices)) {
                    return false;
                }
                phiIndices.clear();
                psiIndices.clear();
                break;
            case PREDICATE:
                return phi.equals(psi);
        }

        for (int i = 0; i < phi.getArity(); i++) {
            if (!testEquality(phi.getSubformula(i), psi.getSubformula(i), new HashSet<>(phiIndices), new HashSet<>(psiIndices))) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testRemoveFreezes() {
        TestFormulae forms = new TestFormulae("downShaker.phi.xml", "downShaker.psi.xml");
        Formula result = StarDownShaker.INSTANCE.downShake(forms.phi());
        Assert.assertTrue(testEquality(forms.psi(), result, new HashSet<Integer>(), new HashSet<Integer>()));
    }
}
