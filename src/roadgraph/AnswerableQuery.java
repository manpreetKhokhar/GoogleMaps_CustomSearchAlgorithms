package roadgraph;

public class AnswerableQuery {
	private int start;
	private int end;
	
	public AnswerableQuery(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public int getFirst() {
		return start;
	}
	
	public int getSecond() {
		return end;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnswerableQuery other = (AnswerableQuery) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnswerableQuery a=new AnswerableQuery(4,10);
		AnswerableQuery b=new AnswerableQuery(10,4);
		AnswerableQuery c=new AnswerableQuery(4,10);
		int hash_a=a.hashCode();
		int hash_b=b.hashCode();
		int hash_c=c.hashCode();
		System.out.printf("hash a= %d  and b=%d and c=%d \n",hash_a,hash_b,hash_c);

	}

}
