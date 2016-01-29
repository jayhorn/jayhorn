
class Unsat01 {
	public void main(String[] args) {
		int i = 2, j = 1;
		i = 3;
		j = 4;

		if (i >= 1 && i <= 11)
			assert j == i + 1;
		else
			assert j == -1;
	}

}
