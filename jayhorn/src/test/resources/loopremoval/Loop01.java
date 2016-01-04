package loopremoval;

import java.util.concurrent.Callable;

public class Loop01 {

//	public int simple() {
//		synchronized (this) {
//			throw new RuntimeException("fuuu");
//		}
//	}
	

	Iterable<Loop01> selfWithAuxiliaryCfs ;
    public <V> V runWithCompactionsDisabled(Callable<V> callable, boolean interruptValidation, boolean interruptViews)
    {
        // synchronize so that concurrent invocations don't re-enable compactions partway through unexpectedly,
        // and so we only run one major compaction at a time
        synchronized (this)
        {

//            for (Loop01 cfs : selfWithAuxiliaryCfs)
//                pause();
            try
            {
                // doublecheck that we finished, instead of timing out
//                for (Loop01 cfs : selfWithAuxiliaryCfs)
//                {
//                    if (!foo())
//                    {
//                        return null;
//                    }
//                }

                // run our task
                try
                {
                    return callable.call();
//                	return null;
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            finally
            {
//                for (Loop01 cfs : selfWithAuxiliaryCfs)
//                    resume();
            }
        }
    }


//	private void pause() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	private boolean foo() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	private void resume() {
//		// TODO Auto-generated method stub
//		
//	}
}
