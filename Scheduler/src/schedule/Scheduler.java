package schedule;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Scheduler {
	private Node node;
	private List<Pod> podList; //�������pod����(��pod����)
	
	public Scheduler(List<Pod> podList, Node node) {
		this.node = node;
		this.podList = podList;
	}
	
	// ģ���˻�
	public int anneal(int cr) {
		int n = podList.size();
		while(true) {
			double max_use = 0; //������Դ������
			double use = 0; //��ǰ����Դ������
			double T = 100.0; //��ʼ�¶�
			double Tmin = 1; //��ֹ�¶�
			double r = 0.999; //��ȴ�ٶ�
			
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
				Collections.swap(new_podList1, n1, n2);  //�����������pod��λ��
				for (int i = 0; i<new_podList1.size();i++) {	//��FCFS���ε���node
					Pod apod = new_podList1.get(i);
					if (!node1.addPod(apod))
						break;
				}
				// ����������
				double uc = node1.getUtilizationofCpu();
				double ur = node1.getUtilizationofRam();	
				if(cr==1) //�Ż�cpu������
					use = uc;
				else if(cr==2) //�Ż�ram������
					use = ur;
				else  //�ۺϿ���cpu��ram
					use = 0.5*(uc+ur);
								
//				System.out.print(freecup+"  "+freeram+"  | "+use+"  "+max_use+"  | ");
				
				// �µ������ʴ��ڵ�ǰmax_use����ֱ�Ӹ���
				if(use>max_use) {
					max_use = use;
					podList1 = new ArrayList<>(new_podList1);
				}
				// ������һ�����ʸ��£����������ֲ����Ž�
				else {
					if(Math.exp(100*(use-max_use)/T) > Math.random()) {
						max_use = use;
						podList1 = new ArrayList<>(new_podList1);
					}
				}
				T = r*T;  //�����¶�
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
			
			for (int i = 0; i<podList1.size();i++) {	//���ε���node
				Pod apod = podList1.get(i);
				if (!node.addPod(apod))
					return 1;
			}
		}
	}
	
	
	
//	// �Ŵ��㷨
//	// �����ʼ��Ⱦɫ�壬n��Ⱦɫ�峤�ȣ�pod������
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
	
	//��������˳��ִ��ģ��FCFS
	public int FCFS() {
		while(true) {
			for (int i = 0; i<podList.size();i++) {	//���ε���node
				Pod apod = podList.get(i);
				if (!node.addPod(apod))	return 1;
			}
		}			
	}
	
	//������� �����pod������ȡһ������node
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
			int k = 0; //������pod��index
			double ds = 1;
			//�ҳ���ǰ������Դ����С��pod
			for (int j = 0; j < podList.size(); j++) {
				//ds:������Դռ��
				ds = podList.get(j).getLimitscpu() * count[j]/node.getTotalcpu() > podList.get(j).getLimitsram()* count[j]/node.getTotalram()
									? podList.get(j).getLimitscpu()* count[j]/node.getTotalcpu() : podList.get(j).getLimitsram()* count[j]/node.getTotalram();
				if (ds < tmp){		
					 tmp = ds;
					 k = j;
				}
			}
			//System.out.printf("��ǰ���������Դռ��Ϊ %.3f",tmp);
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
