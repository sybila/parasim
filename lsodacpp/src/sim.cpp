#include <cstdio>
#include <cstdlib>

#include "LsodaCpp.hpp"

#include "org_sybila_parasim_computation_simulation_lsoda_LsodaWrapper.hpp"

template <typename RealType> class Functor {
public:
    void initialize(int *const &lens, char ** const &tree,
                    RealType **const &constants, int **const &variables) {
        stack = (RealType*)malloc(lens[0] * sizeof(RealType));
        this->lens = lens;
        this->tree = tree;
        this->constants = constants;
        this->variables = variables;
    }

    void operator()(const int &dims, const RealType &t, RealType *const &y, RealType *ydot) {
        for (int dim = 0; dim < dims; ++dim) {

            int stackPos = 0;
            char *localTree = tree[dim];
            RealType *localConstants = constants[dim];
            int *localVariables = variables[dim];

            for (int i = lens[dim] - 1; i >= 0; --i) {

                switch (localTree[i]) {

                case 'C':
                    stack[stackPos] = localConstants[i];
                    break;

                case 'V':
                    stack[stackPos] = y[localVariables[i] - 1];
                    break;

                case '-':
                    stackPos -= 2;
                    stack[stackPos] = stack[stackPos + 1] - stack[stackPos];
                    break;

                case '+':
                    stackPos -= 2;
                    stack[stackPos] = stack[stackPos + 1] + stack[stackPos];
                    break;

                case '*':
                    stackPos -= 2;
                    stack[stackPos] = stack[stackPos + 1] * stack[stackPos];
                    break;

                case '/':
                    stackPos -= 2;
                    stack[stackPos] = stack[stackPos + 1] / stack[stackPos];
                    break;

                case '^':
                    stackPos -= 2;
                    stack[stackPos] = pow(stack[stackPos + 1], stack[stackPos]);
                    break;

                default:
                    fprintf(stderr, "error: undefined node");
                    ydot[dim] = 0;
                }
                ++stackPos;
            }

            ydot[dim] = stack[stackPos - 1];
        }
    }

    void destroy() { free(stack); }

private:
    RealType    *stack;
    int         *lens;
    char       **tree;
    RealType   **constants;
    int        **variables;
};

struct LsodaResult {
    jclass cls;
    jmethodID ctorID;
    jfieldID outputID;
    jfieldID failedID;
    jfieldID errorMsgID;
};

/*
 * ===================== IMPLEMENTATION ==================================
 * Class:     org_sybila_parasim_computation_simulation_lsoda_LsodaWrapper
 * Method:    wrappedFunc
 */
JNIEXPORT jobject JNICALL Java_org_sybila_parasim_computation_simulation_lsoda_LsodaWrapper_wrappedFunc
  (     JNIEnv * env,
        jobject ch,
        jobjectArray tree,
        jobjectArray constants,
        jobjectArray variables,
        jfloatArray initialValues,
        jfloat startTime,
        jfloat timeStep,
        jlong maxSteps,
        jfloat maxRelativeError,
        jfloatArray maxAbsoluteErrors) {

      LsodaResult lsodaResult;

    lsodaResult.cls = env->FindClass("org/sybila/parasim/computation/simulation/lsoda/LsodaResult");
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        return NULL;
    }

    lsodaResult.ctorID = env->GetMethodID(lsodaResult.cls, "<init>", "()V");
    lsodaResult.outputID = env->GetFieldID(lsodaResult.cls, "output", "[F");
    lsodaResult.failedID = env->GetFieldID(lsodaResult.cls, "failed", "J");
    lsodaResult.errorMsgID = env->GetFieldID(lsodaResult.cls, "errorMsg", "Ljava/lang/String;");
    jobject R = env->NewObject(lsodaResult.cls, lsodaResult.ctorID);

    int variableCount = env->GetArrayLength(tree);
    int * iLengths = (int*)malloc(variableCount * sizeof(int));
    char ** iTree = (char**)malloc(variableCount * sizeof(char*));
    float ** iConstants = (float**)malloc(variableCount * sizeof(float*));
    int ** iVariables = (int**)malloc(variableCount * sizeof(int*));
    float * output = (float*)malloc(variableCount * (maxSteps+1) * sizeof(float));


    // fetch data from java
    float *cInitialValues = env->GetFloatArrayElements((jfloatArray)initialValues, NULL);
    float *cMaxAbsoluteErrors = env->GetFloatArrayElements((jfloatArray)maxAbsoluteErrors, NULL);
    float cMaxRelativeError = maxRelativeError;
    
    jobject rowTree;
    jobject rowConstant;
    jobject rowVariable;
    for (int i=0; i<variableCount; ++i) {
        rowTree = env->GetObjectArrayElement(tree, i);
        iTree[i] = (char*)env->GetByteArrayElements((jbyteArray)rowTree, NULL);
        
        rowConstant = env->GetObjectArrayElement(constants, i);
        iConstants[i] = env->GetFloatArrayElements((jfloatArray)rowConstant, NULL);
        
        rowVariable = env->GetObjectArrayElement(variables, i);
        iVariables[i] = env->GetIntArrayElements((jintArray)rowVariable, NULL);
        
        iLengths[i] = (int)env->GetArrayLength((jbyteArray)rowTree);
    }

    // lsoda block
    Functor<float> functor;
    LsodaCpp::SolveParameters<float> params;
    functor.initialize(iLengths, iTree, iConstants, iVariables);

    params.itol = LsodaCpp::ITOL::RTOL_SCALAR_ATOL_ARRAY;
    params.maxAbsoluteErrors = cMaxAbsoluteErrors;
    params.maxRelativeErrors = &cMaxRelativeError;
    params.variableCount = variableCount;
    params.startTime = startTime;
    params.timeStep = timeStep;
    params.maxSteps = maxSteps;
    params.initialValues = cInitialValues;

    LsodaCpp::Result<float> result = LsodaCpp::solve(functor, params, output);

    if (result.code == LsodaCpp::CODE::OK) {
        // prepare return array
        jfloatArray out = env->NewFloatArray(variableCount * (maxSteps+1));
        env->SetFloatArrayRegion(out, 0, variableCount * (maxSteps+1), output );

        env->SetObjectField(R, lsodaResult.outputID, out);
        env->SetIntField(R, lsodaResult.failedID, 0);
    } else {
        env->SetIntField(R, lsodaResult.failedID, 1);
        env->SetObjectField(R, lsodaResult.errorMsgID, env->NewStringUTF(result.msg.c_str()));
    }

    // release ArrayElements
    env->ReleaseFloatArrayElements((jfloatArray)initialValues, cInitialValues, JNI_ABORT);
    env->ReleaseFloatArrayElements((jfloatArray)maxAbsoluteErrors, cMaxAbsoluteErrors, JNI_ABORT);
    for (int i=0; i<variableCount; ++i) {
        rowTree = env->GetObjectArrayElement(tree, i);
        env->ReleaseByteArrayElements((jbyteArray)rowTree, (jbyte*)iTree[i], JNI_ABORT);
        rowConstant = env->GetObjectArrayElement(constants, i);
        env->ReleaseFloatArrayElements((jfloatArray)rowConstant, iConstants[i], JNI_ABORT);
        rowVariable = env->GetObjectArrayElement(variables, i);
        env->ReleaseIntArrayElements((jintArray)rowVariable, iVariables[i], JNI_ABORT);
    }

    // free native memory
    free(iLengths);
    free(iTree);
    free(iConstants);
    free(iVariables);
    free(output);

    return R;
}
