package schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node {
	private double totalCpu;
	private double totalRam;
	private double preCpu;
	private double preRam;
	private int count;
	private List<Pod> podList;
	public Node(double cpu, double ram) {
		this.totalCpu = cpu;
		this.totalRam = ram;
		this.count = 0;
		podList = new ArrayList<Pod>();
	}
	public boolean addPod(Pod p) {
		if (p.getResquestcpu() <= totalCpu - preCpu && p.getResquestram() <= totalRam - preRam) {
			podList.add(p);
			p.setState(1);
			preCpu += p.getLimitscpu();
			preRam += p.getLimitsram();
			count ++;
			return true;
		}
		else
			return false;
	}
	public int getcount() {
		return this.count;
	}
	public double getTotalcpu() {
		return this.totalCpu;
	}
	public double getTotalram() {
		return this.totalRam;
	}
	public double getPrecpu() {
		return this.preCpu;
	}
	public double getPreram() {
		return this.preRam;
	}
	public List<Pod> getPodlist() {
		return this.podList;
	}
	
	public double getUtilizationofCpu() {
		return preCpu / totalCpu;
	}
	
	public double getUtilizationofRam() {
		return preRam / totalRam;
	}
}
