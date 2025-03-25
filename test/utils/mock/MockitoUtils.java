package utils.mock;

import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.util.ThreadPoolExecutorUtils;
import com.serotonin.mango.web.ContextWrapper;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static com.serotonin.mango.util.ThreadPoolExecutorUtils.createForkJoinPool;
import static org.mockito.Mockito.*;

public class MockitoUtils {

    public static BackgroundProcessing mockBackgroundProcessing() {
        BackgroundProcessing backgroundProcessing = mock(BackgroundProcessing.class);
        ForkJoinPool forkJoinPool = createForkJoinPool();
        when(backgroundProcessing.getCommonPool()).thenReturn(forkJoinPool);
        doAnswer(a -> {
            ThreadPoolExecutorUtils.joinTermination(forkJoinPool, "test", 1, TimeUnit.MILLISECONDS);
            return null;
        }).when(backgroundProcessing)
                .terminate();
        return backgroundProcessing;
    }

    public static ContextWrapper mockContextWrapper(BackgroundProcessing backgroundProcessing) {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        when(contextWrapper.getBackgroundProcessing()).thenReturn(backgroundProcessing);
        return contextWrapper;
    }
}
