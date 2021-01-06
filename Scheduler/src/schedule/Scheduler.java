package schedule;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Scheduler {
	private Node node;
	private List<Pod> podList; //待分配的pod队列(按pod种类)
	
	public Scheduler(List<Pod> podList, Node node) {
		this.node = node;
		this.podList = podList;
	}
	
	// 模拟退火
	public int anneal(int cr) {
		int n = podList.size();
		while(true) {
			double max_use = 0; //最大的资源利用率
			double use = 0; //当前的资源利用率
			double T = 100.0; //初始温度
			double Tmin = 1; //终止温度
			double r = 0.999; //冷却速度
			
			List<Pod> podList1 = new ArrayList<>(podList);
			double freecpu = node.getTotalcpu() - node.getPrecpu();
			double freeram = node.getTotalram() - node.getPreram();
			
//			System.out.println( node.getPrecpu()+"  "+ node.getPreram()+"  "+freecpu+"  "+freeram);
			
			while(T>Tmin) {
				List<Pod> new_podList1 = new ArrayList<>(podList1);			
				Node node1 = new Node(freecpu, freeram);
				int n1 = 0;
				int n2 = 0;
				Random rand = new Random();
				while(n1==n2) {
					n1 = rand.nextInt(n);
					n2 = rand.nextInt(n);
				}
				Collections.swap(new_podList1, n1, n2);  //随机交换两个pod的位置
				for (int i = 0; i<new_podList1.size();i++) {	//按FCFS依次调入node
					Pod apod = new_podList1.get(i);
					if (!node1.addPod(apod))
						break;
				}
				// 计算利用率
				double uc = node1.getUtilizationofCpu();
				double ur = node1.getUtilizationofRam();	
				if(cr==1) //优化cpu利用率
					use = uc;
				else if(cr==2) //优化ram利用率
					use = ur;
				else  //综合考虑cpu和ram
					use = 0.5*(uc+ur);
								
//				System.out.print(freecup+"  "+freeram+"  | "+use+"  "+max_use+"  | ");
				
				// 新的利用率大于当前max_use，则直接更新
				if(use>max_use) {
					max_use = use;
					podList1 = new ArrayList<>(new_podList1);
				}
				// 否则，以一定概率更新，用来跳出局部最优解
				else {
					if(Math.exp(100*(use-max_use)/T) > Math.random()) {
						max_use = use;
						podList1 = new ArrayList<>(new_podList1);
					}
				}
				T = r*T;  //降低温度
			}
			
//			System.out.println(node.getPrecpu()+"  "+node.getPreram());
//			for(int i=0; i<n;i++) {
//				System.out.print(podList1.get(i).getResquestcpu()+"  ");
//			}
//			System.out.println();
//			for(int i=0; i<n;i++) {
//				System.out.print(podList1.get(i).getResquestram()+"  ");
//			}
//			System.out.println();
			
			for (int i = 0; i<podList1.size();i++) {	//依次调入node
				Pod apod = podList1.get(i);
				if (!node.addPod(apod))
					return 1;
			}
		}
	}
	
	
	
//	// 遗传算法
//	// 随机初始化染色体，n：染色体长度（pod个数）
//	public String[] init(int n) {
//		Random rand=new Random();
//		String[] chromosome_states = new String[4];	
//		for(int i=0; i<4; i++) {
//			StringBuilder state = new StringBuilder();
//			for(int j=0; j<n; j++) {
//				state.append(rand.nextInt(2));
//			}
//			chromosome_states[i] = state.toString();
//			System.out.println(chromosome_states[i]);
//		}
//		return chromosome_states;
//	}
//	public boolean is_finished(double[][] fitnesses) {
//		
//	}
//	public double[][] fitness(String[] chromosome_states){
//		double[][] fitnesses;
//		fitnesses =new double [2][];
//		for(int i=0; i<chromosome_states.length; i++) {
//			double cpu_sum = 0;
//			double ram_sum = 0;
//			String state = chromosome_states[i];
//			for(int j=0; j<state.length(); j++) {
//				if(state.charAt(j)==1) {
//					Pod apod = podList.get(j);
//					cpu_sum += apod.getResquestcpu();
//					ram_sum += apod.getResquestram();
//				}
//			}
//			fitnesses[0][i] = cpu_sum;
//			fitnesses[1][i] = ram_sum;
//		}
//		return fitnesses;
//	}
	
	//这里用以顺序执行模仿FCFS
	public int FCFS() {
		while(true) {
			for (int i = 0; i<podList.size();i++) {	//依次调入node
				Pod apod = podList.get(i);
				if (!node.addPod(apod))	return 1;
			}
		}			
	}
	
	//随机调度 随机从pod队列里取一个放入node
	public int RND() {
			while(true) {
				Random r = new Random();
				int i = r.nextInt(podList.size());
				Pod apod = podList.get(i);
				if (!node.addPod(apod))	return 1;
			}			
	}
		
	public int DRF() {
		int[] count = new int[podList.size()];
		Random r = new Random();
		int i = r.nextInt(podList.size());
		Pod apod = podList.get(i);
		if (!node.addPod(apod))	{
			return 1;
		}
		else {
			count[i]++;
		}
		
		while(true) {
			double tmp = 2;
			int k = 0; //带调度pod的index
			double ds = 1;
			//找出当前主导资源比最小的pod
			for (int j = 0; j < podList.size(); j++) {
				//ds:主导资源占比
				ds = podList.get(j).getLimitscpu() * count[j]/node.getTotalcpu() > podList.get(j).getLimitsram()* count[j]/node.getTotalram()
									? podList.get(j).getLimitscpu()* count[j]/node.getTotalcpu() : podList.get(j).getLimitsram()* count[j]/node.getTotalram();
				if (ds < tmp){		
					 tmp = ds;
					 k = j;
				}
			}
			//System.out.printf("当前最低主导资源占比为 %.3f",tmp);
			//System.out.println();
			apod = podList.get(k);
			if (!node.addPod(apod))	{
				return 1;
			}
			else {
				count[k]++;
			}
		}
	}
}
