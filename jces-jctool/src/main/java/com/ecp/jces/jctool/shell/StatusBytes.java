package com.ecp.jces.jctool.shell;

public class StatusBytes {
	
	private String status;
	private int minCode;
	private int maxCode;
	
	
	public StatusBytes(String status) {
		this.status = status.toLowerCase();
		this.status = this.status.trim();
		
		StringBuffer minCodeStr = new StringBuffer(); 
		StringBuffer maxCodeStr = new StringBuffer(); 
		String sb;
		
		for (int i = 0; i < 4; i++) {
			if (i < this.status.length()) {
				sb = this.status.substring(i, i + 1);
				
				if (sb.matches("[0-9Ff]+")) {
					minCodeStr.append(sb);
					maxCodeStr.append(sb);
				} else {
					minCodeStr.append("0");
					maxCodeStr.append("F");
				}

			}
		}
		
		minCode = Integer.parseInt(minCodeStr.toString(), 16);
		maxCode = Integer.parseInt(maxCodeStr.toString(), 16);

	}

	public String getStatus() {
		return status;
	}

	public int getMinCode() {
		return minCode;
	}
	
	public int getMaxCode() {
		return maxCode;
	}
	
    public boolean equals(Object obj) {
    	if (obj instanceof StatusBytes) {
			StatusBytes sb = (StatusBytes) obj;
			if (sb.getStatus().equals(this.status)) {
				return true;
			}
		}
    	return false;
    }
	
    
    public static void main(String args[]) {
    	
    	String str = "9100,9200,9300,9400,9500,9700,9800,99xx,9axx";
    	if (str.matches("([0-9A-Fa-fXx]{4},)*[0-9A-Fa-fXx]{4}$")) {
    		System.out.println("success.");
    	} else {
    		System.out.println("error");
    	}
    	StatusBytes sb = new StatusBytes("9F00t");
    	
    }
}
