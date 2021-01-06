package schedule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FinalTest {

	public static void main(String[] args) throws IOException {
//		System.out.println(Math.random());
		Pod a = new Pod(1,50);
		Pod b = new Pod(5,30);
		Pod c = new Pod(15,15);
		Pod d = new Pod(30,5);
		Pod e = new Pod (50,1);
		
		List<Pod> podList1 = Arrays.asList(a,b,c,d,e);
		
		File f = new File("output.txt");
		if(!f.exists()) f.createNewFile();
		PrintStream mytxt=new PrintStream(f);
		PrintStream out = System.out;
		for (int j = 1;j <=50; j++) {
			Node node1 = new Node(50*j,50*j);
			List<Pod> podList2 = new ArrayList<>();
			//随机生成待分配pod队列
			//Random r = new Random();
			/*for (int i = 0; i < 100; i++) {
				double cpu = r.nextDouble()*10;
				double ram = r.nextDouble()*10;
				podList2.add(new Pod(cpu,ram));
			}	*/
				Scheduler s = new Scheduler(podList1, node1);
				
				long startTime=System.currentTimeMillis();
				
//				s.FCFS();	//先来先服务
				//s.RND();	//随机调度
				//s.DRF();  //DRF
				s.anneal(0); //模拟退火
				
				long endTime=System.currentTimeMillis();
				
				System.setOut(out);
				System.out.print("NodeSize:"+50*j+"  ");
				int count = node1.getcount();
				long time = endTime-startTime;
				double uc =node1.getUtilizationofCpu();
				double ur =node1.getUtilizationofRam();
				System.out.printf("cpu利用率："+"%.3f"+",  ",uc);
				System.out.printf("ram利用率："+"%.3f"+",  ",ur);
				System.out.printf("pod数："+"%d"+",  ", count);
				System.out.printf("所用时间：" + time + "ms");
				System.out.println();
				
				System.setOut(mytxt);
				System.out.printf("%d %.3f %.3f %d %d", 50*j,uc,ur,count,time);
				System.out.println();
				
		}
	}

}
