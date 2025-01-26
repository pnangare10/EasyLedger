package com.example.demo.util;

public class NumToWords {

	private static final String[] tensNames = {
		    "",
		    " Ten",
		    " Twenty",
		    " Thirty",
		    " Forty",
		    " Fifty",
		    " Sixty",
		    " Seventy",
		    " Eighty",
		    " Ninety"
		  };
	
	private static final String[] numNames = {
		    "",
		    " One",
		    " Two",
		    " Three",
		    " Four",
		    " Five",
		    " Six",
		    " Seven",
		    " Eight",
		    " Nine",
		    " Ten",
		    " Eleven",
		    " Twelve",
		    " Thirteen",
		    " Fourteen",
		    " Fifteen",
		    " Sixteen",
		    " Seventeen",
		    " Eighteen",
		    " Nineteen"
		  };
	
	private static final String[] hundreds = {
			" Hundred",
			" Thousand",
			" Lakh",
			" Crore"
	};
	
	public static String convert(Integer n) {
		String word = "";
		String str = String.valueOf(n);
		char ch[] = str.toCharArray();
		Integer len = str.length();
		if (len > 1) {
			for (int i = len - 1; i >= 0; i--) {
//			for(int i = 0; i< len-1 ; i++) {
				// Two digit
				if (i == len - 2) {
					if (str.charAt(len - 2) - '0' < 2) {
						word = word + numNames[Integer.parseInt(str.substring(len - 2, len))];
					} else {
						word += tensNames[Integer.parseInt(str.substring(len - 2, len - 1))];
						word = word + numNames[Integer.parseInt(str.substring(len - 1, len))];
					}
				}
				// Hundred
				else if (i == len - 3) {
					if (word.strip().equals("")) {
					} else {
						word = " and" + word;
					}
					if (str.substring(i, i + 1).equals("0")) {

					} else {
						word = hundreds[0] + word;
						word = numNames[Integer.parseInt(str.substring(i, i + 1))] + word;
					}
				}
				// Thousand
				else if (i == len - 4 && i == 0) {
					word = hundreds[1] + word;
					word = numNames[Integer.parseInt(str.substring(i, i + 1))] + word;
				}

				else if (i == len - 5) {
					word = hundreds[1] + word;
					if (str.charAt(i) - '0' < 2) {
						word = numNames[Integer.parseInt(str.substring(i, i + 2))] + word;
					} else {
						word = numNames[Integer.parseInt(str.substring(i + 1, i + 2))] + word;
						word = tensNames[Integer.parseInt(str.substring(i, i + 1))] + word;

					}
				}

				// Lakh
				else if (i == len - 6 && i == 0) {
					word = hundreds[2] + word;
					word = numNames[Integer.parseInt(str.substring(i, i + 1))] + word;
				}

				// Lakh
				else if (i == len - 7) {
					word = hundreds[2] + word;
					if (str.charAt(i) - '0' < 2) {
						word = numNames[Integer.parseInt(str.substring(i, i + 2))] + word;
					} else {
						word = numNames[Integer.parseInt(str.substring(i + 1, i + 2))] + word;
						word = tensNames[Integer.parseInt(str.substring(i, i + 1))] + word;

					}
				}
				// Crore
				else if (i == len - 8 && i == 0) {
					word = hundreds[3] + word;
					word = numNames[Integer.parseInt(str.substring(i, i + 1))] + word;
				}

				// crore
				else if (i == len - 9) {
					word = hundreds[3] + word;
					if (str.charAt(i) - '0' < 2) {
						word = numNames[Integer.parseInt(str.substring(i, i + 2))] + word;
					} else {
						word = numNames[Integer.parseInt(str.substring(i + 1, i + 2))] + word;
						word = tensNames[Integer.parseInt(str.substring(i, i + 1))] + word;

					}
				}

			}
		} else {
			return numNames[Integer.valueOf(str)];
		}
		return word;
	}

}
