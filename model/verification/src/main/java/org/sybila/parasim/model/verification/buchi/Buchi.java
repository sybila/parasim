/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification.buchi;

import java.util.Set;
import java.util.Iterator;
import java.util.TreeSet;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.Property;

/**
 * Represents an LTL property Buchi automaton to express properties of numerical
 * simulations with discrete states.
 *
 * Gives methods to compute a run starting at the initialStates
 * and taking further transitions to successor states through transitions
 * enabled in given points of a numerical simulation.
 *
 * If there are any derivatives in atomic propositions of guards, the
 * hasDerivatives() method returns true, in this case it is mandatory that
 * all guards are evaluated on PointDerivatives not only on Points.
 *
 * @author Sven Drazan <sven@mail.muni.cz>
 */
public class Buchi implements Property {

    private int stateCount;             /* number of states of automaton */

    private String[] stateNames;        /* names of all states */

    private Set<Integer> initialStates; /* set of initial states of atomaton */

    private Set<Integer> acceptingStates; /* set of accepting states */

    private Transition[][] transitions; /* transitions of each state */

    private boolean hasDerivatives;     /* flag to indicate wheather variable
    derivatives are present in atomic
    propositions, can be used for checking
    if Points should be wrapped into
    PointDerivatives */

    private boolean hasTautologyGuards; /* true if there are any null guards */


    public Buchi() {
        stateCount = 0;
        stateNames = null;
        initialStates = null;
        acceptingStates = null;
        transitions = null;
        hasDerivatives = false;
        hasTautologyGuards = false;
    }

    /**
     * Tries to load an Buchi automaton from file.
     * On success all fields are filled with correct data and null is returned,
     * on failure an error message is returned and fields are unmodified.
     *
     * @param fileName file with property
     * @param ode ODE system used in case of variable derivatives in atomic
     *            propositions of transition guard formulae
     * @return null on success, error string on failure
     */
    public String loadFromFile(String fileName, OdeSystem ode) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("The parameter fileName is null or empty.");
        }
        if (ode == null) {
            throw new IllegalArgumentException("The parameter ode is null.");
        }
        BuchiParser parser = new BuchiParser();
        if (parser.parseFile(fileName, ode)) {
            stateCount = parser.getStateCount();
            stateNames = parser.getStateNames();
            initialStates = parser.getInitialStates();
            acceptingStates = parser.getAcceptingStates();
            transitions = parser.getTransitions();
            hasDerivatives = parser.getHasDerivatives();
            hasTautologyGuards = parser.getHasTautologyGuards();
        } else {
            return parser.getError();
        }

        return null;
    }

    /**
     * Evaluates guards on all transitions of the given state and returns a set
     * of all possible successors.
     *
     * @param state state who's successors are to be computed
     * @param p point in which to evaluated enabledness of transitions
     * @return set of all successors
     */
    public Set<Integer> getSuccessors(int state, Point p) {
        Set<Integer> succs = new TreeSet<Integer>();

        for (int i = 0; i < transitions[state].length; i++) {
            if (transitions[state][i].enabled(p)) {
                succs.add(new Integer(transitions[state][i].getTo()));
            }
        }
        return succs;
    }

    public int getStateCount() {
        return stateCount;
    }

    public boolean hasDerivatives() {
        return hasDerivatives;
    }

    public boolean hasTautologyGuards() {
        return hasTautologyGuards;
    }

    /**
     * Evaluates guards on all transitions of the given set of states and
     * returns a set of all possible successors. Evaluation is lazy ie. if
     * a successor is already in the set all transitions leading into it
     * will not be evaluated.
     *
     * @param states set of states who's successors are to be computed
     * @param p point in which to evaluate the enabledness of transitions
     * @return set of all successors
     */
    public Set<Integer> getSuccessors(Set<Integer> states, Point p) {
        Set<Integer> succs = new TreeSet<Integer>();
        for (Iterator<Integer> it = states.iterator(); it.hasNext();) {
            int state = it.next();
            for (int i = 0; i < transitions[state].length; i++) {
                if (!succs.contains(transitions[state][i].getTo())) {
                    if (transitions[state][i].enabled(p)) {
                        succs.add(transitions[state][i].getTo());
                    }
                }
            }
        }
        return succs;
    }

    /**
     * Returns the set of enabled transitions in point p.
     * @param p the point in which to evaluate enabled transitions
     */
    /*public Set<Transition> get_enabled_transitions(Point p)
    {
    Set<Transition> result = new TreeSet<Transition>();
    for (int i=0; i<transitions.length; i++)
    {
    for (int j=0; j<transitions[i].length; j++)
    {
    if (transitions[i][j].enabled(p))
    {
    result.add(transitions[i][j]);
    }
    }
    }
    return result;
    }*/
    /**
     * Returns true if there are any "always true" null guards coming out of
     * the set of states.
     * @param states set of states to look for null transition guards
     * @return true if there exists a null guard in transitions from given set
     */
    public boolean tautologyGuards(Set<Integer> states) {
        for (Iterator<Integer> it = states.iterator(); it.hasNext();) {
            int state = it.next();
            for (int j = 0; j < transitions[state].length; j++) {
                if (transitions[state][j].getGuard() == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<Integer> getInitialStates() {
        return initialStates;
    }

    public Set<Integer> getAcceptingStates() {
        return acceptingStates;
    }

    /**
     * Returns the intersection with the acceptingStates.
     */
    /*public int[] intersect_accepting_states(int[] states)
    {
    int[] inter = new int[acceptingStates.length];
    int inter_count = 0;
    for (int i=0; i<acceptingStates.length; i++)
    {
    for (int j=0; j<states.length; j++)
    {
    if (acceptingStates[i] == states[j])
    {
    inter[inter_count] = states[j];
    inter_count++;
    break;
    }
    }
    }
    inter = (int[]) Utils.expand(inter, inter_count);
    return inter;
    }*/
    /**
     * Loads a default property G true.
     */
    public void setDefaultProperty() {
        stateCount = 1;
        stateNames = new String[]{"q"};
        initialStates = new TreeSet();
        initialStates.add(new Integer(0));
        acceptingStates = new TreeSet();
        acceptingStates.add(new Integer(0));
        transitions = new Transition[1][1];
        transitions[0][0] = new Transition(0, 0, null);
        //transitionCount = 1;
        hasDerivatives = false;

        /*
        Variable v1 = new Variable(ode, 0);
        VariableDerivative v2 = new VariableDerivative(ode, 1);
        AtomicProposition ap1 = new AtomicProposition(v1, AtomicPropOperator.AP_GREATER, v2);
        AtomicProposition ap2 = new AtomicProposition(v2, AtomicPropOperator.AP_LESS, v1);
        Evaluable[] garr = new Evaluable[]{ap1, ap2};
        EvaluableGroup g = new EvaluableGroup(garr,EvaluableGroupType.AP_GROUP_AND);
        ArrayPoint p = new ArrayPoint(new float[2], 3);
        g.evaluate(p);
         */
    }

    @Override
    public String toString() {
        String s = "Buchi automaton {\n";
        s += "states " + stateNames.toString() + ";\n";
        s += "init " + initialStates.toArray().toString() + ";\n";
        s += "accept " + acceptingStates.toArray().toString() + ";\n";
        for (int i = 0; i < transitions.length; i++) {
            for (int j = 0; j < transitions[i].length; j++) {
                s += stateNames[i] + " -> " + stateNames[transitions[i][j].getTo()]
                        + " {" + transitions[i][j].getGuard().toString() + "}\n";
            }
        }
        return s + "}\n";
    }
}
