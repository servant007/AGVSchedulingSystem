package schedulingSystem.toolKit;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

/**
 * @desc:
 * @since Apr 17, 2013
 * @author chaisson 
 *org.apache.log4j.DailyRollingFileAppender
 * <p>
 */
public class MyAppender extends DailyRollingFileAppender {
	
    @Override
	public boolean isAsSevereAsThreshold(Priority priority) {  
		  //ֻ�ж��Ƿ���ȣ������ж����ȼ�   
		return this.getThreshold().equals(priority);  
	}  
}
