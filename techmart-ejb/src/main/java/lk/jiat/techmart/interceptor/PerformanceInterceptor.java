package lk.jiat.techmart.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import lk.jiat.techmart.store.TechMartStore;

public class PerformanceInterceptor {
    @AroundInvoke
    public Object monitor(InvocationContext context) throws Exception {
        try {
            Object result = context.proceed();
            TechMartStore.incrementMetric("EJB Invocations");
            return result;
        } catch (Exception e) {
            TechMartStore.incrementMetric("Orders Failed");
            throw e;
        }
    }
}
