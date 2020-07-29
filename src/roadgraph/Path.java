package roadgraph;

import java.util.ArrayList;
import java.util.List;

import geography.GeographicPoint;

public class Path implements Comparable<Path>{
	private List<GeographicPoint> path;
	private double benefitPerSize=0d;
	private double incrementalBenefit=0d;
	
	public Path(List<GeographicPoint> path) {
		this.path=path;
	}

	public List<GeographicPoint> getPath() {
		return path;
	}
	
	public int getPathSize() {
		if(path!=null)
			return path.size();
		return 0;
	}

	public double getBenefitPerSize() {
		return benefitPerSize;
	}

	public void setBenefitPerSize(double benefitPerSize) {
		this.benefitPerSize = benefitPerSize;
	}

	public double getIncrementalBenefit() {
		return incrementalBenefit;
	}

	public void setIncrementalBenefit(double incrementalBenefit) {
		this.incrementalBenefit = incrementalBenefit;
	}	
	
	public Path getSubPath(GeographicPoint a,GeographicPoint b) {
		int fromIndex=path.indexOf(a);
		int toIndex=path.indexOf(b);
		if(fromIndex<toIndex)
			return new Path(path.subList(fromIndex, toIndex));
		else return null;
	}

	@Override
	public int compareTo(Path p) {
		if(this.incrementalBenefit<p.incrementalBenefit)return 1;
		else if(this.incrementalBenefit>p.incrementalBenefit)return -1;
		else return 0;

	}

	@Override
	public String toString() {
		return "Path:"+path.toString();
	}

}
