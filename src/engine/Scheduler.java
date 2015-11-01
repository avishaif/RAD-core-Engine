package engine;

import core.ProcessData;
import core.ThreadData;
import facade.Facade;

/**
 * Setting configuration data.
 * The Scheduler can be invoked periodically if appropriate attributes set.
 * Uses RAD-Core-Lib to set affinity and priority.
 *
 */
public class Scheduler implements Runnable
{
	private boolean continuous = false;
	int sleepTime = 0;

	/**
	 * Constructor.
	 * @param loop
	 * 			True if the scheduler should be invoked periodically. False if the scheduler should be invoked once.
	 * @param sleepTime
	 * 			If loop set to true, this parameters defines the invoke interval.
	 */
	public Scheduler(boolean loop, int sleepTime)
	{
		this.continuous = loop;
		this.sleepTime = sleepTime;
	}

	/**
	 * Runs the scheduler.
	 */
	public void run()
	{
		
		do
		{
			System.out.println("setting config. data");
			for (ProcessData process : ConfigurationData.getData())
			{
				if (process.getAffinity() != null)
					Facade.setProcessAffinity(process.getName(),process.getAffinity());
				if (process.getPriority() != -1)
					Facade.setProcessPriority(process.getName(),process.getPriority());
				if (process.getThreads().size() > 0)
				{
					for (ThreadData thread : process.getThreads())
					{
						if (thread.getAffinity() != null)
							Facade.setThreadAffinity(process.getName(), thread.getName(),thread.getAffinity(), thread.isJavaThread());
						if (thread.getPriority() != -1)
							Facade.setThreadPriority(process.getName(), thread.getName(),thread.getPriority(), thread.isJavaThread());
					}
				}
			}
			System.out.println("data set \n");
			try
			{
				Thread.sleep(sleepTime);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		} while (continuous);
		
	}
}
