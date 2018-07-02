/**
 * Copyright (c) 2018 Ales Pejznoch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * This is C++ implementation of LSODA based on C source made by
 * Hon Wah Tam in 1991:
 * http://www.ccl.net/cca/software/SOURCES/C/kinetics2/lsoda-dir/
 *
 * and on the 12 November 2003 original fortran source of LSODA
 * from Alan C. Hindmarsh and Linda R. Petzold:
 * http://www.netlib.org/odepack/
 *
 * The original source code came without an license. Hereby I release this
 * code under the MIT license.
 *
 * Ales Pejznoch <pejznoch@gmail.com>
 * https://gitlab.com/alespejznoch/LsodaCpp
 */

/**
 * DLSODA: Livermore Solver for Ordinary Differential Equations, with
 *         Automatic method switching for stiff and nonstiff problems.
 *
 *
 * DLSODA solves the initial value problem for stiff or nonstiff
 * systems of first order ODEs,
 *     dy/dt = f(t,y) ,  or, in component form,
 *     dy(i)/dt = f(i) = f(i,t,y(1),y(2),...,y(NEQ)) (i = 1,...,NEQ).
 *
 * This a variant version of the DLSODE package.
 * It switches automatically between stiff and nonstiff methods.
 * This means that the user does not have to determine whether the
 * problem is stiff or not, and the solver will automatically choose the
 * appropriate method.  It always starts with the nonstiff method.
 *
 * Authors:       Alan C. Hindmarsh
 *                Center for Applied Scientific Computing, L-561
 *                Lawrence Livermore National Laboratory
 *                Livermore, CA 94551
 * and
 *                Linda R. Petzold
 *                Univ. of California at Santa Barbara
 *                Dept. of Computer Science
 *                Santa Barbara, CA 93106
 *
 * References:
 * 1.  Alan C. Hindmarsh,  ODEPACK, A Systematized Collection of ODE
 *     Solvers, in Scientific Computing, R. S. Stepleman et al. (Eds.),
 *     North-Holland, Amsterdam, 1983, pp. 55-64.
 * 2.  Linda R. Petzold, Automatic Selection of Methods for Solving
 *     Stiff and Nonstiff Systems of Ordinary Differential Equations,
 *     Siam J. Sci. Stat. Comput. 4 (1983), pp. 136-148.
 */

#ifndef LSODACPP_HPP
#define LSODACPP_HPP

#include <algorithm>
#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <string>
#include <type_traits>


namespace LsodaCpp {

    enum class CODE {
        OK,
        /**
         * means an excessive amount of work (more than mxstep
         * steps) was done on this call, before completing the
         * requested task, but the integration was otherwise
         * successful as far as reachedTime.  (mxstep is an
         * optional input and is normally 500.)
         * To continue, the user may simply call nextStep again
         * (the excess work step counter will be reset to 0).
         * In addition, the user may increase mxstep to avoid
         * this error return (see below on optional inputs).
         */
        TOO_MANY_INTERNAL_STEPS,
        /**
         * means too much accuracy was requested for the precision
         * of the machine being used.  This was detected before
         * completing the requested task, but the integration
         * was successful as far as reachedTime.  To continue,
         * the tolerance parameters must be reset.
         * The optional output TOLSF may be used for this
         * purpose.  (Note: If this condition is detected before
         * taking any steps, then an ILLEGAL_INPUT return
         * occurs instead.)
         */
        TOO_MUCH_ACCURACY,
        /**
         * means illegal input was detected, before taking any
         * integration steps.  See written message for details.
         * Note:  If the solver detects an infinite loop of calls
         * to the solver with illegal input, it will cause
         * the run to stop.
         */
        ILLEGAL_INPUT,
        /**
         * means there were repeated error test failures on
         * one attempted step, before completing the requested
         * task, but the integration was successful as far
         * as reachedTime. The problem may have a singularity,
         * or the input may be inappropriate.
         */
        REPEATED_ERROR_TEST_FAILURES,
        /**
         * means there were repeated convergence test failures on
         * one attempted step, before completing the requested
         * task, but the integration was successful as far
         * as reachedTime. This may be caused by an inaccurate
         * Jacobian matrix, if one is being used.
         */
        REPEATED_CONVERGENCE_TEST_FAILURES,
        /**
         * means ewtVector(i) became zero for some i during the
         * integration.  Pure relative error control (atol(i)=0.0)
         * was requested on a variable which has now vanished.
         * The integration was successful as far as reachedTime.
         */
        BAD_ERROR_CONTROL,
        /**
         * means use of inappropriate function call
         */
        BAD_USE
    };

    /**
     * \brief Result status return type
     */
    template<typename RealType>
    struct Result {
        Result(){}
        Result(CODE code, std::string msg) :
            code(code), msg(msg), reachedTime(0) {}
        Result(CODE code, std::string msg, float reachedTime):
            code(code), msg(msg), reachedTime(reachedTime) {}
        Result(CODE code, std::string msg, double reachedTime):
            code(code), msg(msg), reachedTime(reachedTime) {}
        Result(CODE code, std::string msg, long double reachedTime):
            code(code), msg(msg), reachedTime(reachedTime) {}
        CODE code = CODE::OK;
        std::string msg;
        RealType reachedTime = 0;
    };
    
    /**
     * \brief an indicator for the type of error control
     *
     *       The input parameters ITOL, RTOL, and ATOL determine
     *    the error control performed by the solver.  The solver will
     *    control the vector E = (E(i)) of estimated local errors
     *    in y, according to an inequality of the form
     *                max-norm of ( E(i)/EWT(i) )   .le.   1,
     *    where EWT = (EWT(i)) is a vector of positive error weights.
     *    The values of RTOL and ATOL should all be non-negative.
     *    The following table gives the types (scalar/array) of
     *    RTOL and ATOL, and the corresponding form of EWT(i).
     *
     *        ITOL  RTOL       ATOL       EWT(i)
     *        1     scalar     scalar     RTOL*ABS(Y(i)) + ATOL
     *        2     scalar     array      RTOL*ABS(Y(i)) + ATOL(i)
     *        3     array      scalar     RTOL(i)*ABS(Y(i)) + ATOL
     *        4     array      array      RTOL(i)*ABS(Y(i)) + ATOL(i)
     *
     *    When either of these parameters is a scalar, it need not
     *    be dimensioned in the user's calling program.
     */
    enum class ITOL {
        RTOL_SCALAR_ATOL_SCALAR,
        RTOL_SCALAR_ATOL_ARRAY,
        RTOL_ARRAY_ATOL_SCALAR,
        RTOL_ARRAY_ATOL_ARRAY
    };

/**
 * \brief Changeable parameters
 *
 * parameters to be set in initialization step of Lsoda
 * or changed later while running
 */
template <typename RealType>
struct Parameters {
    static_assert(std::is_floating_point<RealType>::value,
                 "class Work can only be instantiated with floating point types");
    
    public:

    /* an indicator for the type of error control */
    ITOL itol = ITOL::RTOL_ARRAY_ATOL_ARRAY;

    RealType * maxRelativeErrors = nullptr;
    RealType * maxAbsoluteErrors = nullptr;


    struct Optional {
    public:
    /**
     * \brief Flag to generate extra printing at method switches.
     *
     * verbose = false means no extra printing (the default).
     * verbose = true means print data on each switch.
     * t, h, and nst will be printed.
     */
    bool verbose = false;

    /**
     * \brief Maximum number of (internally defined) steps
     * allowed during one call to the solver.
     *
     * The default value is 500.
     */
    int mxstep = 500;

    /**
     * \brief Maximum number of messages printed (per problem)
     * warning that T + H = T on a step (H = step size).
     *
     * This must be positive to result in a non-default
     * value.  The default value is 10.
     */
    int mxhnil = 10;

    /**
     * \brief Critical value of t which the solver is not to overshoot.
     *
     * Required if ITASK is
     * 4 or 5, and ignored otherwise.  (See ITASK.)
     */
    RealType tcrit = 0;


    /**
     * \brief The maximum absolute step size allowed.
     * 
     * The default value (0) means infinite.
     */
    RealType hmax = 0;

    /**
     * \brief The minimum absolute step size allowed.
     *
     * The default value is 0.  (This lower bound is not
     * enforced on the final step before reaching TCRIT
     * when ITASK = 4 or 5.)
     */
    RealType hmin = 0;
    } optional;
};

/**
 * \brief initialization parameters used by the init() function
 */
template <typename RealType>
struct InitialParameters : public Parameters<RealType> {

    static_assert(std::is_floating_point<RealType>::value,
                 "class Work can only be instantiated with floating point types");

    public:

    RealType startTime = 0;
    int variableCount = 0;
    int mxordn = 12;
    int mxords = 5;
    RealType *initialValues = nullptr;

    /**
     * \brief The step size to be attempted on the first step.
     *
     * The default value is determined by the solver.
     */
    RealType initialH0 = 0;
};

/**
 * \brief parameters to be used by the solve() function
 */
template <typename RealType>
struct SolveParameters : public InitialParameters<RealType> {

    static_assert(std::is_floating_point<RealType>::value,
                 "class Work can only be instantiated with floating point types");

    public:
    RealType timeStep = 0.05;
    int64_t maxSteps = 1000;
};

/**
 * \brief forward declaration of the Lsoda class
 */
template <typename RealType>
class Lsoda;

/**
 * \brief solve ODEsystem and store results in output
 *
 * \param[in]  ODEsystem functor or function - see example
 * \param[in]  params
 * \param[out]  output preallocated pointer
 */
template <typename RealType, typename Func>
Result<RealType> solve(  Func &ODEsystem,
            const SolveParameters<RealType> &params,
            RealType *const &output) {

    Result<RealType> result;
    Lsoda<RealType> solver;

    // copy 0. step
    for (int i=0; i<params.variableCount; ++i) {
        output[i] = params.initialValues[i];
    }

    result = solver.init(params);
    if (result.code != CODE::OK) {
        result.reachedTime = params.startTime;
        return result;
    }

    RealType nextTime = params.startTime + params.timeStep;

    for (int64_t i = 1; i <= params.maxSteps; ++i) {
        result = solver.lsodaNextStep(ODEsystem, 1, nextTime, &output[i*params.variableCount]);
        if (result.code != CODE::OK) {
            return result;
        }
        nextTime = nextTime + params.timeStep;
    }

    return result;
}






template <typename RealType> class Lsoda {
    static_assert(
        std::is_floating_point<RealType>::value,
        "class Lsoda can only be instantiated with floating point types");

public:
    Lsoda() : stoda(*this), eta(computeEta()), sqrteta(std::sqrt(eta)),
    state(1), itask(0), dims(0), yhSize(0), ccmax(0), el0(0), h(0),
    hu(0), rc(0), tn(0), hmxi(0), t(0), tsw(0), pdnorm(0), h0(0),
    initialized(0), nhnil(0), nslast(0), jstart(0), kflag(0), l(0),
    meth(0), miter(0), maxord(0), maxcor(0), msbp(0), mxncf(0), n(0),
    nq(0), nst(0), nfe(0), nje(0), nqu(0), mused(0), ml(0),
    output(nullptr), yhMatrix(nullptr), wmMatrix(nullptr),
    ewtVector(nullptr), savfVector(nullptr), 
    acorVector(nullptr), ipvtVector(nullptr)
    {}

    ~Lsoda() {
        freeVectors();
    }

    Result<RealType> init(const InitialParameters<RealType> &initialParameters);
    Result<RealType> changeConfiguration(const Parameters<RealType> &parameters);

    template <typename Func>
    Result<RealType> lsodaNextStep(Func &ODEsystem, const int &itask,
                                   const RealType &nextTime,
                                   RealType *const &output);

    Parameters<RealType> getChangeableParameters();
    std::string getWarningMsg();
    std::string getInfoMsg();

private:
    RealType computeEta() {
        RealType eta = std::nextafter(static_cast<RealType>(1), static_cast<RealType>(2));
        eta -= static_cast<RealType>(1);
        return eta;
    }

    template <typename Func>
    void lsodaMainLoop(Func &ODEsystem, const RealType &nextTime);

    void checkInitialParameters(const InitialParameters<RealType> &initialParameters);
    void setInitialParameters(const InitialParameters<RealType> &initialParameters);
    void checkChangeableParameters(const Parameters<RealType> &parameters);
    void setChangeableParameters(const Parameters<RealType> &parameters);
    void alwaysCheck(const int &itask, const RealType &tout);

    RealType vmnorm(RealType *const &v);
    void setOutput();
    void successfulReturn(const bool &ihit);
    bool initVectors();
    void freeVectors();
    void ewset(RealType *const &ycur);
    void intdy(const RealType &tout, int &iflag);
    void intdys(const RealType &tout);
    void intdyb(const RealType &tout);

    template <typename Func>
    void firstCall(Func &ODEsystem, const RealType &tout);
    bool nextCall(const RealType &tout);
    bool checkBeforeStoda();
    bool handleStodaSuccess(const RealType &tout);
    void handleStodaFailure();





    InitialParameters<RealType> configuration;

    class Stoda;
    Stoda stoda;

    RealType eta, sqrteta;

    unsigned int state;

    int itask;
    int dims;
    int yhSize;

    RealType ccmax, el0, h, hu, rc, tn, hmxi, t, tsw, pdnorm, h0;
    int initialized, nhnil, nslast, jstart, kflag, l, meth, miter, maxord,
        maxcor, msbp, mxncf, n, nq, nst, nfe, nje, nqu, mused, ml;

    RealType *output;
    /* variables for various vectors and the Jacobian. */
    RealType **yhMatrix, **wmMatrix, *ewtVector, *savfVector, *acorVector;
    int *ipvtVector;

    CODE errorCode = CODE::OK;
    std::string errorMsg, warningMsg, infoMsg;

    const RealType sm1[13] = {0,   0.5,  0.575, 0.55,  0.45, 0.35, 0.25,
                              0.2, 0.15, 0.1,   0.075, 0.05, 0.025};

    static thread_local constexpr unsigned int MAX_NQ = 100;
};







/***********************************************
 * Public method definitions
 **********************************************/

template <typename RealType>
Result<RealType> Lsoda<RealType>::init(const InitialParameters<RealType> &initialParameters) {

    errorCode = CODE::OK;

    if (state != 1) {
        return Result<RealType>{CODE::BAD_USE,
                                "Lsoda has already been initialized."};
    }

    checkInitialParameters(initialParameters);
    if (errorCode != CODE::OK) {
        return Result<RealType>{errorCode, errorMsg};
    }

    checkChangeableParameters(
        static_cast<Parameters<RealType>>(initialParameters));
    if (errorCode != CODE::OK) {
        return Result<RealType>{errorCode, errorMsg};
    }

    setInitialParameters(initialParameters);
    setChangeableParameters(
        static_cast<Parameters<RealType>>(initialParameters));

    yhSize = 1 + std::max(configuration.mxordn, configuration.mxords);
    if (!initVectors()) {
        freeVectors();
        return Result<RealType>{CODE::ILLEGAL_INPUT,
                                "insufficient memory for your problem"};
    }

    initialized = 0;
    meth = 1;

    return Result<RealType>{CODE::OK, {}};
}

template <typename RealType>
Result<RealType> Lsoda<RealType>::changeConfiguration(
    const Parameters<RealType> &parameters) {

    errorCode = CODE::OK;

    checkChangeableParameters(parameters);
    if (errorCode != CODE::OK) {
        return Result<RealType>{errorCode, errorMsg};
    }

    setChangeableParameters(parameters);

    /*
       set flag to signal parameter changes to stoda.
    */
    jstart = -1;

    return {CODE::OK, {}};
}

template <typename RealType>
template <typename Func>
Result<RealType> Lsoda<RealType>::lsodaNextStep(Func &ODEsystem, const int &itask,
                                    const RealType &nextTime, RealType *const &output) {

    errorCode = CODE::OK;
    // legacy arrays starts from 1
    this->output = output - 1;

    alwaysCheck(itask, nextTime);
    if (errorCode != CODE::OK) {
        return {errorCode, errorMsg, t};
    }

    this->itask = itask;

    if (state == 1) {
        firstCall(ODEsystem, nextTime);
        if (errorCode != CODE::OK) {
            return {errorCode, errorMsg, t};
        }
    }

    if (state == 2 || state == 3) {
        if (nextCall(nextTime)) {
            return {errorCode, errorMsg, t};
        }
    }

    lsodaMainLoop(ODEsystem, nextTime);
    if (errorCode != CODE::OK) {
        return {errorCode, errorMsg, t};
    }

    return {CODE::OK, {}, t};
}

template <typename RealType>
Parameters<RealType> Lsoda<RealType>::getChangeableParameters() {
    return configuration;
}
        

template <typename RealType>
std::string Lsoda<RealType>::getWarningMsg() {
    return warningMsg;
}


template <typename RealType>
std::string Lsoda<RealType>::getInfoMsg() {
    return infoMsg;
}















/***********************************************
 * Private method definitions
 **********************************************/

template <typename RealType>
template <typename Func>
void Lsoda<RealType>::lsodaMainLoop(Func &ODEsystem, const RealType &nextTime) {

    while (true) {
        if (checkBeforeStoda()) {
            return;
        }

        stoda.runStoda(ODEsystem);

        if (kflag == 0) {
            if (handleStodaSuccess(nextTime)) {
                return;
            }
            continue;
        }

        if (kflag != 0) {
            handleStodaFailure();
            return;
        }
    }
}

template <typename RealType>
void Lsoda<RealType>::setOutput()
{
    for (int i = 1; i <= n; i++) {
        output[i] = yhMatrix[1][i];
    }
    t = tn;
}

template <typename RealType>
void Lsoda<RealType>::successfulReturn(const bool &ihit)
{
    for (int i = 1; i <= n; i++) {
        output[i] = yhMatrix[1][i];
    }
    t = tn;
    if (itask == 4 || itask == 5) {
        if (ihit) {
            t = configuration.optional.tcrit;
        }
    }
    state = 2;
}

template <typename RealType>
void Lsoda<RealType>::alwaysCheck(const int &itask, const RealType &tout) {

    if (itask < 1 || itask > 5) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "Itask = " + std::to_string(itask) + "is illegal.";
        return;
    }

    if (initialized == 0 && (state == 2 || state == 3)) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "State > 1 but lsoda not initialized.";
        return;
    }

    if (tout == t) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "NextTime = t. run aborted..";
        return;
    }
}


template <typename RealType>
void Lsoda<RealType>::checkInitialParameters(
    const InitialParameters<RealType> &initialParameters) {

    if (initialParameters.variableCount < 1) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "VariableCount = " +
                   std::to_string(initialParameters.variableCount) +
                   "is less than 1";
        return;
    }

    if (initialParameters.mxordn < 0) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "Mxordn = " +
                   std::to_string(initialParameters.mxordn) +
                   "is less than 0";
        return;
    }

    if (initialParameters.mxords < 0) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "Mxords = " +
                   std::to_string(initialParameters.mxords) +
                   "is less than 0";
        return;
    }

    configuration.initialH0 = initialParameters.initialH0;
}

template <typename RealType>
void Lsoda<RealType>::setInitialParameters(
    const InitialParameters<RealType> &initialParameters) {

    configuration.variableCount = this->dims = this->n = initialParameters.variableCount;
    if (initialParameters.mxordn > 0) {
        configuration.mxordn = std::min(configuration.mxordn, initialParameters.mxordn);
    }
    if (initialParameters.mxordn > 0) {
        configuration.mxords = std::min(configuration.mxords, initialParameters.mxords);
    }
    configuration.initialValues = initialParameters.initialValues - 1;
    t = configuration.startTime = initialParameters.startTime;
}


template <typename RealType>
void Lsoda<RealType>::checkChangeableParameters(
    const Parameters<RealType> &parameters) {

    /*
        Check rtol and atol for legality.
    */
    RealType &rtoli = parameters.maxRelativeErrors[0];
    RealType &atoli = parameters.maxAbsoluteErrors[0];
    for (int i = 0; i < configuration.variableCount; i++) {
        if (parameters.itol == ITOL::RTOL_ARRAY_ATOL_SCALAR ||
            parameters.itol == ITOL::RTOL_ARRAY_ATOL_ARRAY) {
            rtoli = parameters.maxRelativeErrors[i];
        }
        if (parameters.itol == ITOL::RTOL_SCALAR_ATOL_ARRAY || 
            parameters.itol == ITOL::RTOL_ARRAY_ATOL_ARRAY) {
            atoli = parameters.maxAbsoluteErrors[i];
        }
        if (rtoli < 0.) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "Rtol = " + std::to_string(rtoli) + "is less than 0.";
            return;
        }
        if (atoli < 0.) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "Atol = " + std::to_string(atoli) + "is less than 0.";
            return;
        }
    }

    /* Optional inputs.   */
    if (parameters.optional.mxstep < 0) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "Mxstep = is less than 0.";
        return;
    }

    if (parameters.optional.mxhnil < 0) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "Mxhnil = is less than 0.";
        return;
    }
    if (parameters.optional.hmax < 0.) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "Hmax = is less than 0.";
        return;
    }

    if (parameters.optional.hmin < 0.) {
        errorCode = CODE::ILLEGAL_INPUT;
        errorMsg = "Hmin = is less than 0.";
        return;
    }
}

template <typename RealType>
void Lsoda<RealType>::setChangeableParameters(const Parameters<RealType> &parameters) {

    configuration.itol = parameters.itol;
    configuration.maxRelativeErrors = parameters.maxRelativeErrors - 1;
    configuration.maxAbsoluteErrors = parameters.maxAbsoluteErrors - 1;

    /* Optional inputs.   */
    configuration.optional.verbose = parameters.optional.verbose;
    
    configuration.optional.mxstep = parameters.optional.mxstep;
    if (configuration.optional.mxstep == 0) {
        configuration.optional.mxstep = 500;
    }
    configuration.optional.mxhnil = parameters.optional.mxhnil;
    if (configuration.optional.mxhnil == 0) {
        configuration.optional.mxhnil = 10;
    }

    configuration.optional.hmax = parameters.optional.hmax;
    hmxi = 0;
    if (configuration.optional.hmax > 0) {
        hmxi = 1. / (parameters.optional.hmax);
    }
    configuration.optional.hmin = parameters.optional.hmin;
    configuration.optional.tcrit = parameters.optional.tcrit;
}

template <typename RealType>
template <typename Func>
void Lsoda<RealType>::firstCall(Func &ODEsystem, const RealType &tout) {
/*
   Block c.
   The next block is for the initial call only ( *istate = 1 ).
   It contains all remaining initializations, the initial call to f,
   and the calculation of the initial step size.
   The error weights in ewtVector are inverted after being loaded.
*/
    RealType ayi = 0, rh = 0, sum = 0, tdist = 0, tol = 0, w0 = 0, atoli = 0;

    if (configuration.initialH0 != 0) {
        if ((tout - configuration.startTime) * configuration.initialH0 <
            0) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "NextTime = " + std::to_string(tout) +
                       "behind t = " + std::to_string(configuration.startTime) +
                       ". integration direction is given by " +
                       std::to_string(configuration.initialH0);
            return;
        }
        h0 = configuration.initialH0;
    }


        tn = t;
        tsw = t;
        maxord = configuration.mxordn;
        if (itask == 4 || itask == 5) {
            if ((configuration.optional.tcrit - tout) * (tout - t) < 0.) {
                errorCode = CODE::ILLEGAL_INPUT;
                errorMsg = "Itask = 4 or 5 and tcrit behind nextTime";
                return;
            }
            if (h0 != 0. && (t + h0 - configuration.optional.tcrit) * h0 > 0.) {
                h0 = configuration.optional.tcrit - t;
            }
        }
        jstart = 0;
        nhnil = 0;
        nst = 0;
        nje = 0;
        nslast = 0;
        hu = 0.;
        nqu = 0;
        mused = 0;
        miter = 0;
        ccmax = 0.3;
        maxcor = 3;
        msbp = 20;
        mxncf = 10;
/*
   Initial call to f.
*/
        (ODEsystem) (n, t, configuration.initialValues + 1, yhMatrix[2] + 1);
        nfe = 1;
/*
   Load the initial value vector in yhMatrix.
*/
        for (int i = 1; i <= n; i++) {
            yhMatrix[1][i] = configuration.initialValues[i];
        }
/*
   Load and invert the ewtVector array.  ( h is temporarily set to 1. )
*/
        nq = 1;
        h = 1.;
        ewset(configuration.initialValues);
        for (int i = 1; i <= n; i++) {
            if (ewtVector[i] <= 0.) {
                errorCode = CODE::ILLEGAL_INPUT;
                errorMsg = "EwtVector[" +
                    std::to_string(i) + "] = " +
                        std::to_string(ewtVector[i]) +
                        " <= 0.";
                return;
            }
            ewtVector[i] = 1. / ewtVector[i];
        }

/*
   The coding below computes the step size, h0, to be attempted on the
   first step, unless the user has supplied a value for this.
   First check that tout - *t differs significantly from zero.
   A scalar tolerance quantity tol is computed, as max(rtol[i])
   if this is positive, or max(atol[i]/fabs(y[i])) otherwise, adjusted
   so as to be between 100*ETA and 0.001.
   Then the computed value h0 is given by

      h0^(-2) = 1. / ( tol * w0^2 ) + tol * ( norm(f) )^2

   where   w0     = max( fabs(*t), fabs(tout) ),
           f      = the initial value of the vector f(t,y), and
           norm() = the weighted vector norm used throughout, given by
                    the vmnorm function routine, and weighted by the
                    tolerances initially loaded into the ewtVector array.

   The sign of h0 is inferred from the initial values of tout and *t.
   fabs(h0) is made < fabs(tout-*t) in any case.
*/
        if (h0 == 0.) {
            tdist = std::fabs(tout - t);
            w0 = std::max(std::fabs(t), std::fabs(tout));
            if (tdist < 2. * eta * w0) {
                errorCode = CODE::ILLEGAL_INPUT;
                errorMsg = "NextTime too close to t to start integration.";
                return;
            }
            tol = configuration.maxRelativeErrors[1];
            if (configuration.itol == ITOL::RTOL_ARRAY_ATOL_SCALAR ||
                configuration.itol == ITOL::RTOL_ARRAY_ATOL_ARRAY) {
                for (int i = 2; i <= n; i++) {
                    tol = std::max(tol, configuration.maxRelativeErrors[i]);
                }
            }
            if (tol <= 0.) {
                atoli = configuration.maxAbsoluteErrors[1];
                for (int i = 1; i <= n; i++) {
                    if (configuration.itol == ITOL::RTOL_SCALAR_ATOL_ARRAY ||
                        configuration.itol == ITOL::RTOL_ARRAY_ATOL_ARRAY) {
                        atoli = configuration.maxAbsoluteErrors[i];
                    } 
                    ayi = std::fabs(configuration.initialValues[i]);
                    if (ayi != 0.) {
                        tol = std::max(tol, atoli / ayi);
                    }
                }
            }
            tol = std::max(tol, static_cast<RealType>(100) * eta);
            tol = std::min(tol, static_cast<RealType>(0.001));
            sum = vmnorm(yhMatrix[2]);
            sum = 1. / (tol * w0 * w0) + tol * sum * sum;
            h0 = 1. / std::sqrt(sum);
            h0 = std::min(h0, tdist);
            h0 = h0 * ((tout - t >= 0.) ? 1. : -1.);
        }        /* end if ( h0 == 0. )   */
        /*
           Adjust h0 if necessary to meet hmax bound.
        */
        rh = std::fabs(h0) * hmxi;
        if (rh > 1.) {
            h0 /= rh;
        }
/*
   Load h with h0 and scale yhMatrix[2] by h0.
*/
        h = h0;
        for (int i = 1; i <= n; i++) {
            yhMatrix[2][i] *= h0;
        }
}


template <typename RealType>
bool Lsoda<RealType>::nextCall(const RealType &tout) {
    /*
       Block d.
       The next code block is for continuation calls only ( *istate = 2 or 3 )
       and is to check stop conditions before taking a step.
    */
    int iflag = 0, ihit = 0;
    RealType tp = 0, hmx = 0, tnext = 0;
    nslast = nst;
    switch (itask) {
    case 1: {
        if ((tn - tout) * h >= 0.) {
            intdy(tout, iflag);
            if (iflag != 0) {
                errorCode = CODE::ILLEGAL_INPUT;
                errorMsg = "Trouble in intdy, itask = " +
                           std::to_string(itask) +
                           ", nextTime = " + std::to_string(tout) +
                           ".";
                return true;
            }
            t = tout;
            state = 2;
            return true;
        }
    } break;
    case 2:
        // continue
        break;
    case 3:
        tp = tn - hu * (1. + 100. * eta);
        if ((tp - tout) * h > 0.) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "Itask = " + std::to_string(itask) +
                       " and tout behind tcur - hu.";
            return true;
        }
        if ((tn - tout) * h < 0.) {
            // continue
            break;
        }
        successfulReturn(ihit);
        return true;
    case 4:
        if ((tn - configuration.optional.tcrit) * h > 0.) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "Itask = 4 or 5 and tcrit is behind tcur.";
            return true;
        }
        if ((configuration.optional.tcrit - tout) * h < 0.) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "Itask = 4 or 5 and tcrit is behind nextTime.";
            return true;
        }
        if ((tn - tout) * h >= 0.) {
            intdy(tout, iflag);
            if (iflag != 0) {
                errorCode = CODE::ILLEGAL_INPUT;
                errorMsg = "Trouble from intdy, itask = " +
                           std::to_string(itask) +
                           ", nextTime = " + std::to_string(tout) +
                           ".";
                return true;
            }
            t = tout;
            state = 2;
            return true;
        }
        break;
    case 5:
        if ((tn - configuration.optional.tcrit) * h > 0.) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "Itask = 4 or 5 and tcrit is behind tcur.";
            return true;
        }
        break;
    } /* end switch   */

    if (itask == 4 || itask == 5) {
        hmx = std::fabs(tn) + std::fabs(h);
        ihit =
            std::fabs(tn - configuration.optional.tcrit) <= (100. * eta * hmx);
        if (ihit > 0) {
            t = configuration.optional.tcrit;
            successfulReturn(ihit);
            return true;
        }
        tnext = tn + h * (1. + 4. * eta);
        if ((tnext - configuration.optional.tcrit) * h <= 0.) {
            return false;
        }
        h = (configuration.optional.tcrit - tn) * (1. - 4. * eta);
        if (state == 2 && jstart >= 0) {
            jstart = -2;
        }
    }

    return false;
}

template <typename RealType> bool Lsoda<RealType>::checkBeforeStoda() {
    /*
       First check for too many steps being taken, update ewtVector ( if not
       at start of problem).  Check for too much accuracy being requested, and
       check for h below the roundoff level in t.
    */
    RealType tolsf = 0;
    if (state != 1 || nst != 0) {
        if ((nst - nslast) >= configuration.optional.mxstep) {
            errorCode = CODE::TOO_MANY_INTERNAL_STEPS;
            errorMsg = "At current t = " + std::to_string(t) +
                       ", mxstep = " +
                       std::to_string(configuration.optional.mxstep) +
                       " steps taken on this call before "
                       "reaching nextTime.";
            setOutput();
            return true;
        }
        ewset(yhMatrix[1]);
        for (int i = 1; i <= n; i++) {
            if (ewtVector[i] <= 0.) {
                errorCode = CODE::BAD_ERROR_CONTROL;
                errorMsg = "At current t = " + std::to_string(t) +
                           ", ewtVector[" + std::to_string(i) +
                           "] has become " + std::to_string(ewtVector[i]) +
                           " <= 0.";
                setOutput();
                return true;
            }
            ewtVector[i] = 1. / ewtVector[i];
        }
    }
    tolsf = eta * vmnorm(yhMatrix[1]);
    if (tolsf > 0.01) {
        tolsf = tolsf * 200.;
        if (nst == 0) {
            errorCode = CODE::ILLEGAL_INPUT;
            errorMsg = "At start of problem, too much accuracy "
                       "requested for precision of machine, "
                       "suggested scaling factor tolsf = " +
                       std::to_string(tolsf) +
                       ".";
            return true;
        }
        errorCode = CODE::TOO_MUCH_ACCURACY;
        errorMsg = "At current t = " + std::to_string(t) +
                   ", too much accuracy requested "
                   "for precision of machine, suggested "
                   "scaling factor tolsf = " +
                   std::to_string(tolsf) +
                   ".";
        setOutput();
        return true;
    }
    if ((tn + h) == tn) {
        nhnil++;
        if (nhnil <= configuration.optional.mxhnil) {
            warningMsg += "Warning. Internal t = " + std::to_string(tn) +
                          " and h = " + std::to_string(h) +
                          " such that in the machine, t + h = t on the next step "
                          "solver will continue anyway.\n";
            if (nhnil == configuration.optional.mxhnil) {
                warningMsg += "Above warnings has been issued " +
                              std::to_string(nhnil) + " times, "
                              "it will not be issued again for this problem.\n";
            }
        }
    }
    return false;
}


template <typename RealType>
bool Lsoda<RealType>::handleStodaSuccess(const RealType &tout) {
    /*
   Block f.
   The following block handles the case of a successful return from the
   core integrator ( kflag = 0 ).
   If a method switch was just made, record tsw, reset maxord,
   set jstart to -1 to signal stoda to complete the switch,
   and do extra printing of data if ixpr = 1.
   Then, in any case, check for stop conditions.
*/
    RealType tnext = 0, hmx = 0;
    int iflag = 0, ihit = 0;
    initialized = 1;
    if (meth != mused) {
        tsw = tn;
        maxord = configuration.mxordn;
        if (meth == 2) {
            maxord = configuration.mxords;
        }
        jstart = -1;
        if (configuration.optional.verbose) {
            if (meth == 2) {
                infoMsg += "A switch to the BDF (stiff) method has occurred ";
            }
            if (meth == 1) {
                infoMsg += "A switch to the Adams (NONstiff) method has occurred ";
            }
            infoMsg += "at t = " + std::to_string(tn) +
                       ", tentative step size h = " + std::to_string(h) +
                       ", step nst = " + std::to_string(nst) + ".\n";
        }
    } /* end if ( meth != mused )   */
    /*
       itask = 1.
       If tout has been reached, interpolate.
    */
    if (itask == 1) {
        if ((tn - tout) * h < 0.) {
            return false;
        }
        intdy(tout, iflag);
        t = tout;
        state = 2;
        return true;
    }
    /*
       itask = 2.
    */
    if (itask == 2) {
        successfulReturn(ihit);
        return true;
    }
    /*
       itask = 3.
       Jump to exit if tout was reached.
    */
    if (itask == 3) {
        if ((tn - tout) * h >= 0.) {
            successfulReturn(ihit);
            return true;
        }
        return false;
    }
    /*
       itask = 4.
       See if tout or tcrit was reached.  Adjust h if necessary.
    */
    if (itask == 4) {
        if ((tn - tout) * h >= 0.) {
            intdy(tout, iflag);
            t = tout;
            state = 2;
            return true;
        }

        hmx = std::fabs(tn) + std::fabs(h);
        ihit =
            std::fabs(tn - configuration.optional.tcrit) <= (100. * eta * hmx);
        if (ihit > 0) {
            successfulReturn(ihit);
            return true;
        }
        tnext = tn + h * (1. + 4. * eta);
        if ((tnext - configuration.optional.tcrit) * h <= 0.) {
            return false;
        }
        h = (configuration.optional.tcrit - tn) * (1. - 4. * eta);
        if (jstart >= 0) {
            jstart = -2;
        }
        return false;
    } /* end if ( itask == 4 )   */
    /*
       itask = 5.
       See if tcrit was reached and jump to exit.
    */
    if (itask == 5) {
        hmx = std::fabs(tn) + std::fabs(h);
        ihit =
            std::fabs(tn - configuration.optional.tcrit) <= (100. * eta * hmx);
        successfulReturn(ihit);
        return true;
    }

    errorCode = CODE::ILLEGAL_INPUT;
    return true;
}

template <typename RealType> void Lsoda<RealType>::handleStodaFailure() {

    errorMsg = "at t = " + std::to_string(tn) +
               " and step size h = " + std::to_string(h) + ", the ";

    if (kflag == -1) {
        errorCode = CODE::REPEATED_ERROR_TEST_FAILURES;
        errorMsg += "error test failed repeatedly or "
                    "with fabs(h) = hmin";

    } else if (kflag == -2) {
        errorCode = CODE::REPEATED_CONVERGENCE_TEST_FAILURES;
        errorMsg += "corrector convergence failed repeatedly or"
                    "with fabs(h) = hmin";
    }
    setOutput();
}


template <typename RealType>
bool Lsoda<RealType>::initVectors() {

    yhMatrix = (RealType **)calloc(1 + yhSize, sizeof(*(yhMatrix)));
    if (yhMatrix == nullptr) {
        return false;
    }
    for (int i = 1; i <= yhSize; ++i) {
        yhMatrix[i] = (RealType *)calloc(1 + dims, sizeof(RealType));
        if (yhMatrix[i] == nullptr) {
            return false;
        }
    }

    wmMatrix = (RealType **)calloc(1 + dims, sizeof(*(wmMatrix)));
    if (wmMatrix == nullptr) {
        return false;
    }
    for (int i = 1; i <= dims; ++i) {
        wmMatrix[i] = (RealType *)calloc(1 + dims, sizeof(RealType));
        if (wmMatrix[i] == nullptr) {
            return false;
        }
    }

    ewtVector = (RealType *)calloc(1 + dims, sizeof(RealType));
    if (ewtVector == nullptr) {
        return false;
    }
    savfVector = (RealType *) calloc(1 + dims, sizeof(RealType));
    if (savfVector == nullptr) {
        return false;
    }
    acorVector = (RealType *) calloc(1 + dims, sizeof(RealType));
    if (acorVector == nullptr) {
        return false;
    }
    ipvtVector = (int *) calloc(1 + dims, sizeof(int));
    if (ipvtVector == nullptr) {
        return false;
    }

    return true;
}

template <typename RealType>
void Lsoda<RealType>::freeVectors()
{
    if (yhMatrix) {
        for (int i = 1; i <= yhSize; ++i) {
            free(yhMatrix[i]);
        }
    }
    if (wmMatrix) {
        for (int i = 1; i <= dims; ++i) {
            free(wmMatrix[i]);
        }
    }

    free(yhMatrix);
    free(wmMatrix);
    free(ewtVector);
    free(savfVector);
    free(acorVector);
    free(ipvtVector);
    yhSize = 0;
    yhMatrix = nullptr; wmMatrix = nullptr;;
    ewtVector = nullptr; savfVector = nullptr; acorVector = nullptr; ipvtVector = nullptr;
}


template <typename RealType>
void Lsoda<RealType>::ewset(RealType *const &ycur)
{
    int             i;

    switch (configuration.itol) {
        case ITOL::RTOL_SCALAR_ATOL_SCALAR:
        for (i = 1; i <= n; i++) {
            ewtVector[i] = configuration.maxRelativeErrors[1] * std::fabs(ycur[i]) + configuration.maxAbsoluteErrors[1];
        }
        break;
        case ITOL::RTOL_SCALAR_ATOL_ARRAY:
        for (i = 1; i <= n; i++) {
            ewtVector[i] = configuration.maxRelativeErrors[1] * std::fabs(ycur[i]) + configuration.maxAbsoluteErrors[i];
        }
        break;
        case ITOL::RTOL_ARRAY_ATOL_SCALAR:
        for (i = 1; i <= n; i++) {
            ewtVector[i] = configuration.maxRelativeErrors[i] * std::fabs(ycur[i]) + configuration.maxAbsoluteErrors[1];
        }
        break;
        case ITOL::RTOL_ARRAY_ATOL_ARRAY:
        for (i = 1; i <= n; i++) {
            ewtVector[i] = configuration.maxRelativeErrors[i] * std::fabs(ycur[i]) + configuration.maxAbsoluteErrors[i];
        }
        break;
    }

}                /* end ewset   */

template <typename RealType>
void Lsoda<RealType>::intdys(const RealType &tout)
{
    RealType s = (tout - tn) / h;
    RealType *yp1 = yhMatrix[l];
    int i = 1;
    for (i = 1; i <= n; i++) {
        output[i] = yp1[i];
    }

    for (int j = nq; j > 0; j--) {
        yp1 = yhMatrix[j];
        for (i = 1; i <= n; i++) {
            output[i] = yp1[i] + s * output[i];
        }
    }
}
template <typename RealType>
void Lsoda<RealType>::intdy(const RealType &tout, int &iflag)
{
    iflag = 0;
    RealType tp = tn - hu - 100. * eta * (tn + hu);
    if ((tout - tp) * (tout - tn) > 0.) {
        iflag = -1;
        return;
    }
    
    if (n > 9) {
        intdyb(tout);
    } else {
        intdys(tout);
    }
}

template <typename RealType>
void Lsoda<RealType>::intdyb(const RealType &tout)
{
    RealType stack[MAX_NQ];
    int i = 2;
    stack[0]=1;
    stack[1]=(tout - tn) / h;
    for (i=2; i<=nq; ++i) {
        stack[i] = stack[i-1]*stack[1];
    }

    RealType *yp1 = yhMatrix[l];
    for (i=1; i<=n; ++i) {
        output[i] = yp1[i]*stack[nq] ;
    }

    for (int j = nq; j > 0; j--) {
        yp1 = yhMatrix[j];
        for (i = 1; i <= n; i++) {
            output[i] += yp1[i]*stack[j-1];
        }
    }
}


template <typename RealType>
RealType Lsoda<RealType>::vmnorm(RealType *const &v)
{
    RealType vm = 0.;
    for (int i = 1; i <= n; i++) {
        vm = std::max(vm, std::fabs(v[i]) * ewtVector[i]);
    }
    return vm;
}


















template <typename RealType>
class Lsoda<RealType>::Stoda {
public:
    explicit Stoda(Lsoda &x) : parent(x) {}

    template <typename Func>
    void runStoda(Func &ODEsystem);

private:
    Lsoda &parent;

    template <typename Func>
    void correction(Func &ODEsystem, const RealType &told, int &corflag,
                    RealType &del, RealType &delp, int &m, int &ierpj,
                    int &iersl, int &jcur);

    template <typename Func>
    void prja(Func &ODEsystem, int &ierpj, int &jcur);

    void resetcoeff();
    void endstoda();
    void orderswitch(int &orderflag);
    void corfailure(const RealType &told, int &corflag);
    void methodswitch();
    void cfode(const int &meth);
    void scaleh();

    RealType fnorm();
    void dgesl();
    void dgefa(int &info);

    void dscal(const int &n, const RealType &da, RealType *const &dx);
    int idamax(const int &n, RealType *const &dx);
    RealType ddot(const int &n, RealType *const &dx, RealType *const &dy);
    void daxpy(const int &n, const RealType &da, RealType *const &dx,
               RealType *const &dy);

    RealType el[14], elco[13][14], tesco[13][4], cm1[13], cm2[6];
    RealType pdest = 0, pdlast = 0, ratio = 0, rh = 0, dsm = 0, rhup = 0,
             pdh = 0, pnorm = 0, conit = 0, crate = 0, hold = 0, rmax = 0;

    int ialth = 0, ipup = 0, lmax = 0, nslp = 0,
        icount = 0, irflag = 0, ncf = 0;
};

template <typename RealType>
template <typename Func>
void Lsoda<RealType>::Stoda::runStoda(Func &ODEsystem) {
    int corflag = 0, orderflag = 0;
    int i = 0, i1 = 0, j = 0, m = 0;
    RealType del = 0, delp = 0, dup = 0, exup = 0, r = 0, told = 0;

    /*
       stoda performs one step of the integration of an initial value
       problem for a system of ordinary differential equations.
       Note.. stoda is independent of the value of the iteration method
       indicator miter, when this is != 0, and hence is independent
       of the type of chord method used, or the Jacobian structure.
       Communication with stoda is done with the following variables:

       jstart = an integer used for input only, with the following
                values and meanings:

                   0  perform the first step,
                 > 0  take a new step continuing from the last,
                  -1  take the next step with a new value of h,
                      n, meth, miter, and/or matrix parameters.
                  -2  take the next step with a new value of h,
                      but with other inputs unchanged.

       kflag = a completion code with the following meanings:

                 0  the step was successful,
                -1  the requested error could not be achieved,
                -2  corrector convergence could not be achieved,
                -3  fatal error in prja.

       miter = corrector iteration method:

                 0  functional iteration,
                >0  a chord method

    */
    parent.kflag = 0;
    told = parent.tn;
    int ierpj = 0;
    int iersl = 0;
    int jcur = 0;
    delp = 0.;

    /*
       On the first call, the order is set to 1, and other variables are
       initialized.  rmax is the maximum ratio by which h can be increased
       in a single step.  It is initially 1.e4 to compensate for the small
       initial h, but then is normally equal to 10.  If a filure occurs
       (in corrector convergence or error test), rmax is set at 2 for
       the next increase.
       cfode is called to get the needed coefficients for both methods.
    */
    if (parent.jstart == 0) {
        lmax = parent.maxord + 1;
        parent.nq = 1;
        parent.l = 2;
        ialth = 2;
        rmax = 10000.;
        parent.rc = 0.;
        parent.el0 = 1.;
        crate = 0.7;
        hold = parent.h;
        nslp = 0;
        ipup = parent.miter;
        /*
           Initialize switching parameters.  meth = 1 is assumed initially.
        */
        icount = 20;
        irflag = 0;
        pdest = 0.;
        pdlast = 0.;
        ratio = 5.;
        cfode(2);
        for (i = 1; i <= 5; i++) {
            cm2[i] = tesco[i][2] * elco[i][i + 1];
        }
        cfode(1);
        for (i = 1; i <= 12; i++) {
            cm1[i] = tesco[i][2] * elco[i][i + 1];
        }
        resetcoeff();
    } /* end if ( jstart == 0 )   */
    /*
       The following block handles preliminaries needed when jstart = -1.
       ipup is set to miter to force a matrix update.
       If an order increase is about to be considered ( ialth = 1 ),
       ialth is reset to 2 to postpone consideration one more step.
       If the caller has changed meth, cfode is called to reset
       the coefficients of the method.
       If h is to be changed, yhMatrix must be rescaled.
       If h or meth is being changed, ialth is reset to l = nq + 1
       to prevent further changes in h for that many steps.
    */
    if (parent.jstart == -1) {
        ipup = parent.miter;
        lmax = parent.maxord + 1;
        if (ialth == 1) {
            ialth = 2;
        }
        if (parent.meth != parent.mused) {
            cfode(parent.meth);
            ialth = parent.l;
            resetcoeff();
        }
        if (parent.h != hold) {
            rh = parent.h / hold;
            parent.h = hold;
            scaleh();
        }
    } /* if ( jstart == -1 )   */
    if (parent.jstart == -2) {
        if (parent.h != hold) {
            rh = parent.h / hold;
            parent.h = hold;
            scaleh();
        }
    } /* if ( jstart == -2 )   */
    /*
       Prediction.
       This section computes the predicted values by effectively
       multiplying the yhMatrix array by the pascal triangle matrix.
       rc is the ratio of new to old values of the coefficient h * el[1].
       When rc differs from 1 by more than ccmax, ipup is set to miter
       to force pjac to be called, if a jacobian is involved.
       In any case, prja is called at least every msbp steps.
    */
    while (true) {
        while (true) {
            if (std::fabs(parent.rc - 1.) > parent.ccmax) {
                ipup = parent.miter;
            }
            if (parent.nst >= nslp + parent.msbp) {
                ipup = parent.miter;
            }
            parent.tn += parent.h;
            RealType *yp1, *yp2;
            for (j = parent.nq; j >= 1; j--) {
                for (i1 = j; i1 <= parent.nq; i1++) {
                    yp1 = parent.yhMatrix[i1];
                    yp2 = parent.yhMatrix[i1 + 1];
                    for (i = 1; i <= parent.n; i++) {
                        yp1[i] += yp2[i];
                    }
                }
            }
            pnorm =
                parent.vmnorm(parent.yhMatrix[1]);

            correction(ODEsystem, told, corflag, del, delp, m,
                       ierpj, iersl, jcur);
            if (corflag == 0) {
                break;
            }
            if (corflag == 1) {
                rh = std::max(rh, parent.configuration.optional.hmin /
                                      std::fabs(parent.h));
                scaleh();
                continue;
            }
            if (corflag == 2) {
                parent.kflag = -2;
                hold = parent.h;
                parent.jstart = 1;
                return;
            }
        } /* end inner while ( corrector loop )   */
          /*
             The corrector has converged.  jcur is set to 0
             to signal that the Jacobian involved may need updating later.
             The local error test is done now.
          */
        jcur = 0;
        if (m == 0) {
            dsm = del / tesco[parent.nq][2];
        }
        if (m > 0) {
            dsm = parent.vmnorm(parent.acorVector) /
                  tesco[parent.nq][2];
        }
        if (dsm <= 1.) {
            /*
               After a successful step, update the yhMatrix array.
               Decrease icount by 1, and if it is -1, consider switching
               methods. If a method switch is made, reset various parameters,
               rescale the yhMatrix array, and exit.  If there is no switch,
               consider changing h if ialth = 1.  Otherwise decrease ialth by 1.
               If ialth is then 1 and nq < maxord, then acorVector is saved for
               use in a possible order increase on the next step.
               If a change in h is considered, an increase or decrease in order
               by one is considered also.  A change in h is made only if it is
               by a factor of at least 1.1.  If not, ialth is set to 3 to
               prevent testing for that many steps.
            */
            parent.kflag = 0;
            parent.nst++;
            parent.hu = parent.h;
            parent.nqu = parent.nq;
            parent.mused = parent.meth;
            RealType *yp1;
            for (j = 1; j <= parent.l; j++) {
                yp1 = parent.yhMatrix[j];
                r = el[j];
                for (i = 1; i <= parent.n; i++) {
                    yp1[i] += r * parent.acorVector[i];
                }
            }
            icount--;
            if (icount < 0) {
                methodswitch();
                if (parent.meth != parent.mused) {
                    rh = std::max(rh, parent.configuration.optional.hmin /
                                          std::fabs(parent.h));
                    scaleh();
                    rmax = 10.;
                    endstoda();
                    break;
                }
            }
            /*
               No method switch is being made.  Do the usual step/order
               selection.
            */
            ialth--;
            if (ialth == 0) {
                rhup = 0.;
                if (parent.l != lmax) {
                    yp1 = parent.yhMatrix[lmax];
                    for (i = 1; i <= parent.n; i++) {
                        parent.savfVector[i] = parent.acorVector[i] - yp1[i];
                    }
                    dup = parent.vmnorm(parent.savfVector) /
                          tesco[parent.nq][3];
                    exup = 1. / static_cast<RealType>(parent.l + 1);
                    rhup = 1. / (1.4 * std::pow(dup, exup) + 0.0000014);
                }
                orderswitch(orderflag);
                /*
                   No change in h or nq.
                */
                if (orderflag == 0) {
                    endstoda();
                    break;
                }
                /*
                   h is changed, but not nq.
                */
                if (orderflag == 1) {
                    rh = std::max(rh, parent.configuration.optional.hmin /
                                          std::fabs(parent.h));
                    scaleh();
                    rmax = 10.;
                    endstoda();
                    break;
                }
                /*
                   both nq and h are changed.
                */
                if (orderflag == 2) {
                    resetcoeff();
                    rh = std::max(rh, parent.configuration.optional.hmin /
                                          std::fabs(parent.h));
                    scaleh();
                    rmax = 10.;
                    endstoda();
                    break;
                }
            } /* end if ( ialth == 0 )   */
            if (ialth > 1 || parent.l == lmax) {
                endstoda();
                break;
            }
            yp1 = parent.yhMatrix[lmax];
            for (i = 1; i <= parent.n; i++) {
                yp1[i] = parent.acorVector[i];
            }
            endstoda();
            break;
        }
        /* end if ( dsm <= 1. )   */
        /*
           The error test failed.  kflag keeps track of multiple failures.
           Restore tn and the yhMatrix array to their previous values, and
           prepare to try the step again.  Compute the optimum step size for
           this or one lower.  After 2 or more failures, h is forced to decrease
           by a factor of 0.2 or less.
         */
        else {
            parent.kflag--;
            parent.tn = told;
            RealType *yp1, *yp2;
            for (j = parent.nq; j >= 1; j--) {
                for (i1 = j; i1 <= parent.nq; i1++) {
                    yp1 = parent.yhMatrix[i1];
                    yp2 = parent.yhMatrix[i1 + 1];
                    for (i = 1; i <= parent.n; i++) {
                        yp1[i] -= yp2[i];
                    }
                }
            }
            rmax = 2.;
            if (std::fabs(parent.h) <=
                parent.configuration.optional.hmin * 1.00001) {
                parent.kflag = -1;
                hold = parent.h;
                parent.jstart = 1;
                break;
            }
            if (parent.kflag > -3) {
                rhup = 0.;
                orderswitch(orderflag);
                if (orderflag == 1 || orderflag == 0) {
                    if (orderflag == 0) {
                        rh = std::min(rh, static_cast<RealType>(0.2));
                    }
                    rh = std::max(rh, parent.configuration.optional.hmin /
                                          std::fabs(parent.h));
                    scaleh();
                }
                if (orderflag == 2) {
                    resetcoeff();
                    rh = std::max(rh, parent.configuration.optional.hmin /
                                          std::fabs(parent.h));
                    scaleh();
                }
                continue;
            }
            /* if ( kflag > -3 )   */
            /*
               Control reaches this section if 3 or more failures have occurred.
               If 10 failures have occurred, exit with kflag = -1.
               It is assumed that the derivatives that have accumulated in the
               yhMatrix array have errors of the wrong order.  Hence the first
               derivative is recomputed, and the order is set to 1.  Then
               h is reduced by a factor of 10, and the step is retried,
               until it succeeds or h reaches hmin.
             */
            else {
                if (parent.kflag == -10) {
                    parent.kflag = -1;
                    hold = parent.h;
                    parent.jstart = 1;
                    break;
                } else {
                    rh = 0.1;
                    rh = std::max(parent.configuration.optional.hmin /
                                      std::fabs(parent.h),
                                  rh);
                    parent.h *= rh;
                    yp1 = parent.yhMatrix[1];
                    for (i = 1; i <= parent.n; i++) {
                        parent.output[i] = yp1[i];
                    }
                    (ODEsystem)(parent.n, parent.tn, parent.output + 1,
                                parent.savfVector + 1);
                    parent.nfe++;
                    yp1 = parent.yhMatrix[2];
                    for (i = 1; i <= parent.n; i++) {
                        yp1[i] = parent.h * parent.savfVector[i];
                    }
                    ipup = parent.miter;
                    ialth = 5;
                    if (parent.nq == 1) {
                        continue;
                    }
                    parent.nq = 1;
                    parent.l = 2;
                    resetcoeff();
                    continue;
                }
            } /* end else -- kflag <= -3 */
        }     /* end error failure handling   */
    }         /* end outer while   */

} /* end runStoda   */

template <typename RealType>
template <typename Func>
void Lsoda<RealType>::Stoda::correction(Func &ODEsystem, const RealType &told,
                                        int &corflag, RealType &del,
                                        RealType &delp, int &m, int &ierpj,
                                        int &iersl, int &jcur) {
    int i = 0;
    RealType rm = 0, rate = 0, dcon = 0;

    /*
       Up to maxcor corrector iterations are taken.  A convergence test is
       made on the r.m.s. norm of each correction, weighted by the error
       weight vector ewtVector.  The sum of the corrections is accumulated in
       the vector acorVector[i].  The yhMatrix array is not altered in the
       corrector loop.
    */

    m = 0;
    corflag = 0;
    rate = 0.;
    del = 0.;
    for (i = 1; i <= parent.n; i++) {
        parent.output[i] = parent.yhMatrix[1][i];
    }
    (ODEsystem)(parent.n, parent.tn, parent.output + 1, parent.savfVector + 1);
    parent.nfe++;
    /*
       If indicated, the matrix P = I - h * el[1] * J is reevaluated and
       preprocessed before starting the corrector iteration.  ipup is set
       to 0 as an indicator that this has been done.
    */
    while (true) {
        if (m == 0) {
            if (ipup > 0) {
                prja(ODEsystem, ierpj, jcur);
                ipup = 0;
                parent.rc = 1.;
                nslp = parent.nst;
                crate = 0.7;
                if (ierpj != 0) {
                    corfailure(told, corflag);
                    return;
                }
            }
            for (i = 1; i <= parent.n; i++) {
                parent.acorVector[i] = 0.;
            }
        } /* end if ( m == 0 )   */
        if (parent.miter == 0) {
            /*
               In case of functional iteration, update y directly from
               the result of the last function evaluation.
            */
            RealType *yp1 = parent.yhMatrix[2];
            for (i = 1; i <= parent.n; i++) {
                parent.savfVector[i] = parent.h * parent.savfVector[i] - yp1[i];
                parent.output[i] = parent.savfVector[i] - parent.acorVector[i];
            }
            del = parent.vmnorm(parent.output);
            yp1 = parent.yhMatrix[1];
            for (i = 1; i <= parent.n; i++) {
                parent.output[i] = yp1[i] + el[1] * parent.savfVector[i];
                parent.acorVector[i] = parent.savfVector[i];
            }
        }
        /* end functional iteration   */
        /*
           In the case of the chord method, compute the corrector error,
           and solve the linear system with that as right-hand side and
           P as coefficient matrix.
         */
        else {

            RealType *yp1 = parent.yhMatrix[2];
            for (i = 1; i <= parent.n; i++) {
                parent.output[i] = parent.h * parent.savfVector[i] -
                                   (yp1[i] + parent.acorVector[i]);
            }
            iersl = 0;
            dgesl();
            del = parent.vmnorm(parent.output);
            yp1 = parent.yhMatrix[1];
            for (i = 1; i <= parent.n; i++) {
                parent.acorVector[i] += parent.output[i];
                parent.output[i] = yp1[i] + el[1] * parent.acorVector[i];
            }
        } /* end chord method   */
          /*
             Test for convergence.  If m > 0, an estimate of the convergence
             rate constant is stored in crate, and this is used in the test.
  
             We first check for a change of iterates that is the size of
             roundoff error.  If this occurs, the iteration has converged, and a
             new rate estimate is not formed.
             In all other cases, force at least two iterations to estimate a
             local Lipschitz constant estimate for Adams method.
             On convergence, form pdest = local maximum Lipschitz constant
             estimate.  pdlast is the most recent nonzero estimate.
          */
        if (del <= 100. * pnorm * parent.eta) {
            break;
        }
        if (m != 0 || parent.meth != 1) {
            if (m != 0) {
                rm = 1024.0;
                if (del <= (1024. * delp)) {
                    rm = del / delp;
                }
                rate = std::max(rate, rm);
                crate = std::max(static_cast<RealType>(0.2) * crate, rm);
            }
            conit = 0.5 / static_cast<RealType>(parent.nq + 2);
            dcon = del *
                   std::min(static_cast<RealType>(1),
                            static_cast<RealType>(1.5) * crate) /
                   (tesco[parent.nq][2] * conit);
            if (dcon <= 1.) {
                pdest = std::max(pdest, rate / std::fabs(parent.h * el[1]));
                if (pdest != 0.) {
                    pdlast = pdest;
                }
                break;
            }
        }
        /*
           The corrector iteration failed to converge.
           If miter != 0 and the Jacobian is out of date, prja is called for
           the next try.   Otherwise the yhMatrix array is retracted to its
           values before prediction, and h is reduced, if possible.  If h cannot
           be reduced or mxncf failures have occured, exit with corflag = 2.
        */
        (m)++;
        if (m == parent.maxcor || (m >= 2 && del > 2. * delp)) {
            if (parent.miter == 0 || jcur == 1) {
                corfailure(told, corflag);
                return;
            }
            ipup = parent.miter;
            /*
               Restart corrector if Jacobian is recomputed.
            */
            m = 0;
            rate = 0.;
            del = 0.;
            for (i = 1; i <= parent.n; i++) {
                parent.output[i] = parent.yhMatrix[1][i];
            }
            (ODEsystem)(parent.n, parent.tn, parent.output + 1,
                        parent.savfVector + 1);
            parent.nfe++;
        }
        /*
           Iterate corrector.
        */
        else {
            delp = del;
            (ODEsystem)(parent.n, parent.tn, parent.output + 1,
                        parent.savfVector + 1);
            parent.nfe++;
        }
    } /* end while   */
} /* end correction   */

template <typename RealType>
template <typename Func>
void Lsoda<RealType>::Stoda::prja(Func &ODEsystem, int &ierpj, int &jcur) {
    int i = 0, j = 0;
    RealType fac = 0, hl0 = 0, r = 0, r0 = 0, yj = 0;
    parent.nje++;
    ierpj = 0;
    jcur = 1;
    hl0 = parent.h * parent.el0;
    /*
       make n calls to f to approximate J.
    */
    fac = parent.vmnorm(parent.savfVector);
    r0 = 1000. * std::fabs(parent.h) * parent.eta *
         static_cast<RealType>(parent.n) * fac;
    if (r0 == 0.) {
        r0 = 1.;
    }
    for (j = 1; j <= parent.n; j++) {
        yj = parent.output[j];
        r = std::max(parent.sqrteta * std::fabs(yj), r0 / parent.ewtVector[j]);
        parent.output[j] += r;
        fac = -hl0 / r;
        (ODEsystem)(parent.n, parent.tn, parent.output + 1,
                    parent.acorVector + 1);
        for (i = 1; i <= parent.n; i++) {
            parent.wmMatrix[i][j] =
                (parent.acorVector[i] - parent.savfVector[i]) * fac;
        }
        parent.output[j] = yj;
    }
    parent.nfe += parent.n;
    /*
       Compute norm of Jacobian.
    */
    parent.pdnorm = fnorm() / std::fabs(hl0);
    /*
       Add identity matrix.
    */
    for (i = 1; i <= parent.n; i++) {
        parent.wmMatrix[i][i] += 1.;
    }
    /*
       Do LU decomposition on P.
    */
    int ier = 0;
    dgefa(ier);
    if (ier != 0) {
        ierpj = 1;
    }
} /* end prja   */

template <typename RealType>
int Lsoda<RealType>::Stoda::idamax(const int &n, RealType *const &dx) {
    int xindex = 0;

    if (n <= 0) {
        return xindex;
    }
    xindex = 1;
    if (n <= 1) {
        return xindex;
    }

    RealType dmax = std::fabs(dx[1]);

    /* Code for increments equal to 1.  */

    RealType xmag;
    for (int i = 2; i <= n; i++) {
        xmag = std::fabs(dx[i]);
        if (xmag > dmax) {
            xindex = i;
            dmax = xmag;
        }
    }
    return xindex;
}

template <typename RealType>
void Lsoda<RealType>::Stoda::dscal(const int &n, const RealType &da,
                                   RealType *const &dx) {
    if (n <= 0) {
        return;
    }

    /* Clean-up loop so remaining vector length is a multiple of 5.  */

    int m = n % 5;
    if (m != 0) {
        for (int i = 1; i <= m; i++) {
            dx[i] = da * dx[i];
        }
        if (n < 5) {
            return;
        }
    }
    for (int i = m + 1; i <= n; i = i + 5) {
        dx[i] = da * dx[i];
        dx[i + 1] = da * dx[i + 1];
        dx[i + 2] = da * dx[i + 2];
        dx[i + 3] = da * dx[i + 3];
        dx[i + 4] = da * dx[i + 4];
    }
}

template <typename RealType>
RealType Lsoda<RealType>::Stoda::ddot(const int &n, RealType *const &dx,
                                      RealType *const &dy) {
    RealType dotprod = 0;
    int i = 0, m = 0;

    dotprod = 0.;
    if (n <= 0) {
        return dotprod;
    }

    /* Clean-up loop so remaining vector length is a multiple of 5.  */

    m = n % 5;
    if (m != 0) {
        for (i = 1; i <= m; i++) {
            dotprod = dotprod + dx[i] * dy[i];
        }
        if (n < 5) {
            return dotprod;
        }
    }
    for (i = m + 1; i <= n; i = i + 5) {
        dotprod = dotprod + dx[i] * dy[i] + dx[i + 1] * dy[i + 1] +
                  dx[i + 2] * dy[i + 2] + dx[i + 3] * dy[i + 3] +
                  dx[i + 4] * dy[i + 4];
    }
    return dotprod;

    /* Code for positive equal nonunit increments.   */

    for (i = 1; i <= n; i = i + 1) {
        dotprod = dotprod + dx[i] * dy[i];
    }
    return dotprod;
}

template <typename RealType>
void Lsoda<RealType>::Stoda::daxpy(const int &n, const RealType &da,
                                   RealType *const &dx, RealType *const &dy) {
    int i = 0, m = 0;

    if (n < 0 || da == 0.) {
        return;
    }

    /* Clean-up loop so remaining vector length is a multiple of 4.  */

    m = n % 4;
    if (m != 0) {
        for (i = 1; i <= m; i++) {
            dy[i] = dy[i] + da * dx[i];
        }
        if (n < 4) {
            return;
        }
    }
    for (i = m + 1; i <= n; i = i + 4) {
        dy[i] = dy[i] + da * dx[i];
        dy[i + 1] = dy[i + 1] + da * dx[i + 1];
        dy[i + 2] = dy[i + 2] + da * dx[i + 2];
        dy[i + 3] = dy[i + 3] + da * dx[i + 3];
    }
    return;
    /* Code for equal, positive, nonunit increments.   */

    for (i = 1; i <= n; i = i + 1) {
        dy[i] = da * dx[i] + dy[i];
    }
}

template <typename RealType> void Lsoda<RealType>::Stoda::dgesl() {
    int k, j;
    RealType t;

    /*
       solve a * x = b.
    */
    /*
       First solve L * y = b.
    */
    for (k = 1; k <= parent.n; k++) {
        t = ddot(k - 1, parent.wmMatrix[k], parent.output);
        parent.output[k] = (parent.output[k] - t) / parent.wmMatrix[k][k];
    }
    /*
       Now solve U * x = y.
    */
    for (k = parent.n - 1; k >= 1; k--) {
        parent.output[k] =
            parent.output[k] +
            ddot(parent.n - k, parent.wmMatrix[k] + k, parent.output + k);
        j = parent.ipvtVector[k];
        if (j != k) {
            t = parent.output[j];
            parent.output[j] = parent.output[k];
            parent.output[k] = t;
        }
    }
}

template <typename RealType> void Lsoda<RealType>::Stoda::dgefa(int &info) {
    int j, k, i;
    RealType t;

    /* Gaussian elimination with partial pivoting.   */

    info = 0;
    for (k = 1; k <= parent.n - 1; k++) {
        /*
           Find j = pivot index.  Note that a[k]+k-1 is the address of
           the 0-th element of the row vector whose 1st element is a[k][k].
        */
        j = idamax(parent.n - k + 1, parent.wmMatrix[k] + k - 1) + k - 1;
        parent.ipvtVector[k] = j;
        /*
           Zero pivot implies this row already triangularized.
        */
        if (parent.wmMatrix[k][j] == 0.) {
            info = k;
            continue;
        }
        /*
           Interchange if necessary.
        */
        if (j != k) {
            t = parent.wmMatrix[k][j];
            parent.wmMatrix[k][j] = parent.wmMatrix[k][k];
            parent.wmMatrix[k][k] = t;
        }
        /*
           Compute multipliers.
        */
        t = -1. / parent.wmMatrix[k][k];
        dscal(parent.n - k, t, parent.wmMatrix[k] + k);
        /*
           Column elimination with row indexing.
        */
        for (i = k + 1; i <= parent.n; i++) {
            t = parent.wmMatrix[i][j];
            if (j != k) {
                parent.wmMatrix[i][j] = parent.wmMatrix[i][k];
                parent.wmMatrix[i][k] = t;
            }
            daxpy(parent.n - k, t, parent.wmMatrix[k] + k,
                  parent.wmMatrix[i] + k);
        }
    } /* end k-loop  */

    parent.ipvtVector[parent.n] = parent.n;
    if (parent.wmMatrix[parent.n][parent.n] == 0.) {
        info = parent.n;
    }
}

template <typename RealType>
void Lsoda<RealType>::Stoda::cfode(const int &meth) {
    int i = 0, nq = 0, nqm1 = 0, nqp1 = 0;
    RealType agamq = 0, fnq = 0, fnqm1 = 0, pc[13], pint = 0, ragq = 0,
             rqfac = 0, rq1fac = 0, tsign = 0, xpin = 0;
    for (RealType &p : pc) {
        p = 0;
    }
    if (meth == 1) {
        elco[1][1] = 1.;
        elco[1][2] = 1.;
        tesco[1][1] = 0.;
        tesco[1][2] = 2.;
        tesco[2][1] = 1.;
        tesco[12][3] = 0.;
        pc[1] = 1.;
        rqfac = 1.;
        for (nq = 2; nq <= 12; nq++) {
            /*
               The pc array will contain the coefficients of the polynomial

                  p(x) = (x+1)*(x+2)*...*(x+nq-1).

               Initially, p(x) = 1.
            */
            rq1fac = rqfac;
            rqfac = rqfac / static_cast<RealType>(nq);
            nqm1 = nq - 1;
            fnqm1 = static_cast<RealType>(nqm1);
            nqp1 = nq + 1;
            /*
               Form coefficients of p(x)*(x+nq-1).
            */
            pc[nq] = 0.;
            for (i = nq; i >= 2; i--) {
                pc[i] = pc[i - 1] + fnqm1 * pc[i];
            }
            pc[1] = fnqm1 * pc[1];
            /*
               Compute integral, -1 to 0, of p(x) and x*p(x).
            */
            pint = pc[1];
            xpin = pc[1] / 2.;
            tsign = 1.;
            for (i = 2; i <= nq; i++) {
                tsign = -tsign;
                pint += tsign * pc[i] / static_cast<RealType>(i);
                xpin += tsign * pc[i] / static_cast<RealType>(i + 1);
            }
            /*
               Store coefficients in elco and tesco.
            */
            elco[nq][1] = pint * rq1fac;
            elco[nq][2] = 1.;
            for (i = 2; i <= nq; i++) {
                elco[nq][i + 1] = rq1fac * pc[i] / static_cast<RealType>(i);
            }
            agamq = rqfac * xpin;
            ragq = 1. / agamq;
            tesco[nq][2] = ragq;
            if (nq < 12) {
                tesco[nqp1][1] = ragq * rqfac / static_cast<RealType>(nqp1);
            }
            tesco[nqm1][3] = ragq;
        } /* end for   */
        return;
    } /* end if ( meth == 1 )   */
    /*
       meth = 2.
    */
    pc[1] = 1.;
    rq1fac = 1.;
    /*
       The pc array will contain the coefficients of the polynomial

          p(x) = (x+1)*(x+2)*...*(x+nq).

       Initially, p(x) = 1.
    */
    for (nq = 1; nq <= 5; nq++) {
        fnq = static_cast<RealType>(nq);
        nqp1 = nq + 1;
        /*
           Form coefficients of p(x)*(x+nq).
        */
        pc[nqp1] = 0.;
        for (i = nq + 1; i >= 2; i--) {
            pc[i] = pc[i - 1] + fnq * pc[i];
        }
        pc[1] *= fnq;
        /*
           Store coefficients in elco and tesco.
        */
        for (i = 1; i <= nqp1; i++) {
            elco[nq][i] = pc[i] / pc[2];
        }
        elco[nq][2] = 1.;
        tesco[nq][1] = rq1fac;
        tesco[nq][2] = static_cast<RealType>(nqp1) / elco[nq][1];
        tesco[nq][3] = static_cast<RealType>(nq + 2) / elco[nq][1];
        rq1fac /= fnq;
    }
} /* end cfode   */

template <typename RealType> void Lsoda<RealType>::Stoda::scaleh() {
    RealType r = 0;
    int j = 0, i = 0;
    /*
       If h is being changed, the h ratio rh is checked against rmax, hmin,
       and hmxi, and the yhMatrix array is rescaled.  ialth is set to l = nq + 1
       to prevent a change of h for that many steps, unless forced by a
       convergence or error test failure.
    */
    rh = std::min(rh, rmax);
    rh = rh / std::max(static_cast<RealType>(1),
                       std::fabs(parent.h) * parent.hmxi * rh);
    /*
       If meth = 1, also restrict the new step size by the stability region.
       If this reduces h, set irflag to 1 so that if there are roundoff
       problems later, we can assume that is the cause of the trouble.
    */
    if (parent.meth == 1) {
        irflag = 0;
        pdh = std::max(std::fabs(parent.h) * pdlast,
                       static_cast<RealType>(0.000001));
        if ((rh * pdh * 1.00001) >= parent.sm1[parent.nq]) {
            rh = parent.sm1[parent.nq] / pdh;
            irflag = 1;
        }
    }
    r = 1.;
    RealType *yp1;
    for (j = 2; j <= parent.l; j++) {
        r *= rh;
        yp1 = parent.yhMatrix[j];
        for (i = 1; i <= parent.n; i++) {
            yp1[i] *= r;
        }
    }
    parent.h *= rh;
    parent.rc *= rh;
    ialth = parent.l;

} /* end scaleh   */

template <typename RealType> RealType Lsoda<RealType>::Stoda::fnorm() {
    int i = 0, j = 0;
    RealType an = 0, sum = 0;

    an = 0.;
    for (i = 1; i <= parent.n; i++) {
        sum = 0.;
        RealType *&ap1 = parent.wmMatrix[i];
        for (j = 1; j <= parent.n; j++) {
            sum += std::fabs(ap1[j]) / parent.ewtVector[j];
        }
        an = std::max(an, sum * parent.ewtVector[i]);
    }
    return an;
}

template <typename RealType>
void Lsoda<RealType>::Stoda::corfailure(const RealType &told, int &corflag) {
    int j = 0, i1 = 0, i = 0;

    ncf++;
    rmax = 2.;
    parent.tn = told;
    RealType *yp1, *yp2;
    for (j = parent.nq; j >= 1; j--) {
        for (i1 = j; i1 <= parent.nq; i1++) {
            yp1 = parent.yhMatrix[i1];
            yp2 = parent.yhMatrix[i1 + 1];
            for (i = 1; i <= parent.n; i++) {
                yp1[i] -= yp2[i];
            }
        }
    }
    if (std::fabs(parent.h) <= parent.configuration.optional.hmin * 1.00001 ||
        ncf == parent.mxncf) {
        corflag = 2;
        return;
    }
    corflag = 1;
    rh = 0.25;
    ipup = parent.miter;
}

template <typename RealType> void Lsoda<RealType>::Stoda::methodswitch() {
    int lm1 = 0, lm1p1 = 0, lm2 = 0, lm2p1 = 0, nqm1 = 0, nqm2 = 0;
    RealType rh1 = 0, rh2 = 0, rh1it = 0, exm2 = 0, dm2 = 0, exm1 = 0, dm1 = 0,
             alpha = 0, exsm = 0;
    if (parent.meth == 1) {
        if (parent.nq > 5) {
            return;
        }
        if (dsm <= (100. * pnorm * parent.eta) || pdest == 0.) {
            if (irflag == 0) {
                return;
            }
            rh2 = 2.;
            nqm2 = std::min(parent.nq, parent.configuration.mxords);
        } else {
            exsm = 1. / static_cast<RealType>(parent.l);
            rh1 = 1. / (1.2 * std::pow(dsm, exsm) + 0.0000012);
            rh1it = 2. * rh1;
            pdh = pdlast * std::fabs(parent.h);
            if ((pdh * rh1) > 0.00001) {
                rh1it = parent.sm1[parent.nq] / pdh;
            }
            rh1 = std::min(rh1, rh1it);
            if (parent.nq > parent.configuration.mxords) {
                nqm2 = parent.configuration.mxords;
                lm2 = parent.configuration.mxords + 1;
                exm2 = 1. / static_cast<RealType>(lm2);
                lm2p1 = lm2 + 1;
                dm2 = parent.vmnorm(parent.yhMatrix[lm2p1]) /
                      cm2[parent.configuration.mxords];

                rh2 = 1. / (1.2 * std::pow(dm2, exm2) + 0.0000012);
            } else {
                dm2 = dsm * (cm1[parent.nq] / cm2[parent.nq]);
                rh2 = 1. / (1.2 * std::pow(dm2, exsm) + 0.0000012);
                nqm2 = parent.nq;
            }
            if (rh2 < ratio * rh1) {
                return;
            }
        }
        /*
           The method switch test passed.  Reset relevant quantities for bdf.
        */
        rh = rh2;
        icount = 20;
        parent.meth = 2;
        parent.miter = 2; // jtyp
        pdlast = 0.;
        parent.nq = nqm2;
        parent.l = parent.nq + 1;
        return;
    } /* end if ( meth == 1 )   */
    /*
       We are currently using a bdf method, considering switching to Adams.
       Compute the step size we could have (ideally) used on this step,
       with the current (bdf) method, and also that for the Adams.
       If nq > mxordn, we consider changing to order mxordn on switching.
       Compare the two step sizes to decide whether to switch.
       The step size advantage must be at least 5/ratio = 1 to switch.
       If the step size for Adams would be so small as to cause
       roundoff pollution, we stay with bdf.
    */
    exsm = 1. / static_cast<RealType>(parent.l);
    if (parent.configuration.mxordn < parent.nq) {
        nqm1 = parent.configuration.mxordn;
        lm1 = parent.configuration.mxordn + 1;
        exm1 = 1. / static_cast<RealType>(lm1);
        lm1p1 = lm1 + 1;
        dm1 =
            parent.vmnorm(parent.yhMatrix[lm1p1]) /
            cm1[parent.configuration.mxordn];
        rh1 = 1. / (1.2 * std::pow(dm1, exm1) + 0.0000012);
    } else {
        dm1 = dsm * (cm2[parent.nq] / cm1[parent.nq]);
        rh1 = 1. / (1.2 * std::pow(dm1, exsm) + 0.0000012);
        nqm1 = parent.nq;
        exm1 = exsm;
    }
    rh1it = 2. * rh1;
    pdh = parent.pdnorm * std::fabs(parent.h);
    if ((pdh * rh1) > 0.00001) {
        rh1it = parent.sm1[nqm1] / pdh;
    }
    rh1 = std::min(rh1, rh1it);
    rh2 = 1. / (1.2 * std::pow(dsm, exsm) + 0.0000012);
    if ((rh1 * ratio) < (5. * rh2)) {
        return;
    }
    alpha = std::max(static_cast<RealType>(0.001), rh1);
    dm1 *= std::pow(alpha, exm1);
    if (dm1 <= 1000. * parent.eta * pnorm) {
        return;
    }
    /*
       The switch test passed.  Reset relevant quantities for Adams.
    */
    rh = rh1;
    icount = 20;
    parent.meth = 1;
    parent.miter = 0;
    pdlast = 0.;
    parent.nq = nqm1;
    parent.l = parent.nq + 1;

} /* end methodswitch   */

template <typename RealType> void Lsoda<RealType>::Stoda::endstoda() {
    RealType r;
    int i;

    r = 1. / tesco[parent.nqu][2];
    for (i = 1; i <= parent.n; i++) {
        parent.acorVector[i] *= r;
    }
    hold = parent.h;
    parent.jstart = 1;
}

template <typename RealType>
void Lsoda<RealType>::Stoda::orderswitch(int &orderflag) {
    int newq = 0, i = 0;
    RealType exsm = 0, rhdn = 0, rhsm = 0, ddn = 0, exdn = 0, r = 0;

    orderflag = 0;

    exsm = 1. / static_cast<RealType>(parent.l);
    rhsm = 1. / (1.2 * std::pow(dsm, exsm) + 0.0000012);

    rhdn = 0.;
    if (parent.nq != 1) {
        ddn = parent.vmnorm(parent.yhMatrix[parent.l]) /
              tesco[parent.nq][1];
        exdn = 1. / static_cast<RealType>(parent.nq);
        rhdn = 1. / (1.3 * std::pow(ddn, exdn) + 0.0000013);
    }
    /*
       If meth = 1, limit rh accordinfg to the stability region also.
    */
    if (parent.meth == 1) {
        pdh = std::max(std::fabs(parent.h) * pdlast,
                       static_cast<RealType>(0.000001));
        if (parent.l < lmax) {
            rhup = std::min(rhup, parent.sm1[parent.l] / pdh);
        }
        rhsm = std::min(rhsm, parent.sm1[parent.nq] / pdh);
        if (parent.nq > 1) {
            rhdn = std::min(rhdn, parent.sm1[parent.nq - 1] / pdh);
        }
        pdest = 0.;
    }
    if (rhsm >= rhup) {
        if (rhsm >= rhdn) {
            newq = parent.nq;
            rh = rhsm;
        } else {
            newq = parent.nq - 1;
            rh = rhdn;
            if (parent.kflag < 0 && rh > 1.) {
                rh = 1.;
            }
        }
    } else {
        if (rhup <= rhdn) {
            newq = parent.nq - 1;
            rh = rhdn;
            if (parent.kflag < 0 && rh > 1.) {
                rh = 1.;
            }
        } else {
            rh = rhup;
            if (rh >= 1.1) {
                r = el[parent.l] / static_cast<RealType>(parent.l);
                parent.nq = parent.l;
                parent.l = parent.nq + 1;
                for (i = 1; i <= parent.n; i++) {
                    parent.yhMatrix[parent.l][i] = parent.acorVector[i] * r;
                }
                orderflag = 2;
                return;
            } else {
                ialth = 3;
                return;
            }
        }
    }
    /*
       If meth = 1 and h is restricted by stability, bypass 10 percent test.
    */
    if (parent.meth == 1) {
        if ((rh * pdh * 1.00001) < parent.sm1[newq]) {
            if (parent.kflag == 0 && rh < 1.1) {
                ialth = 3;
                return;
            }
        }
    } else {
        if (parent.kflag == 0 && rh < 1.1) {
            ialth = 3;
            return;
        }
    }
    if (parent.kflag <= -2) {
        rh = std::min(rh, static_cast<RealType>(0.2));
    }
    /*
       If there is a change of order, reset nq, l, and the coefficients.
       In any case h is reset according to rh and the yhMatrix array is
       rescaled. Then exit or redo the step.
    */
    if (newq == parent.nq) {
        orderflag = 1;
        return;
    }
    parent.nq = newq;
    parent.l = parent.nq + 1;
    orderflag = 2;

} /* end orderswitch   */

template <typename RealType>
void Lsoda<RealType>::Stoda::resetcoeff()
/*
   The el vector and related constants are reset
   whenever the order nq is changed, or at the start of the problem.
*/
{
    int i = 0;
    RealType *ep1 = nullptr;

    ep1 = elco[parent.nq];
    for (i = 1; i <= parent.l; i++) {
        el[i] = ep1[i];
    }
    parent.rc = parent.rc * el[1] / parent.el0;
    parent.el0 = el[1];
    conit = 0.5 / static_cast<RealType>(parent.nq + 2);
}


} // namespace end

#endif // LSODACPP_CPP
