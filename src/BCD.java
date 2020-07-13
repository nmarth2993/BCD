/*
 * Nicholas Marthinuss
 * https://github.com/nmarth2993
 * BCD-6 Program
 * 10/5/18
 */

public class BCD {

    private int[] digits;
    private boolean negative;
    // TODO: integrate negative flag

    public BCD(int[] bcdDigits) {
        digits = new int[bcdDigits.length];
        for (int i = 0; i < bcdDigits.length; i++) {
            digits[i] = bcdDigits[i];
        }
    }

    public BCD(int num) {
        int digit = num % 10;
        digits = new int[] { digit };
        num /= 10;
        while (num > 0) {
            digit = num % 10;
            addADigit(digit);
            num /= 10;
        }
    }

    public int numberOfDigits() {
        return digits.length;
    }

    public int nthDigit(int n) {
        if (n >= digits.length) {
            return 0;
        }
        if (n < 0) {
            n = 0;
        }
        return digits[n];
    }

    public void addADigit(int newDigit) {
        int[] newNum = new int[digits.length + 1];
        for (int i = 0; i < digits.length; i++) {
            newNum[i] = digits[i];
        }
        newNum[newNum.length - 1] = newDigit;
        digits = newNum;
    }

    public BCD addBCDs(BCD other) {
        int digit;
        int remain;
        digit = nthDigit(0) + other.nthDigit(0);
        remain = digit;
        if (digit >= 10) {
            digit %= 10;
        }
        remain /= 10;
        BCD sum = new BCD(digit);

        for (int i = 1; i < Math.max(numberOfDigits(), other.numberOfDigits()); i++) {
            remain += nthDigit(i) + other.nthDigit(i);
            digit = remain;
            if (remain >= 10) {
                digit %= 10;
            }
            remain /= 10;
            sum.addADigit(digit);
        }
        if (remain != 0) {
            sum.addADigit(remain);
        }

        return sum;
    }

    public boolean checkUnderflow(BCD arg, int index) {
        return (nthDigit(index) - arg.nthDigit(index) < 0);
    }

    public int difference(BCD arg, boolean borrow, int index) {
        return (nthDigit(index) - (arg.nthDigit(index) + (borrow ? 10 : 0)));
    }

    public BCD subtractBCDs(BCD arg) {
        BCD result;
        boolean borrow = false;
        borrow = checkUnderflow(arg, 0);
        result = new BCD(nthDigit(0) - arg.nthDigit(0) + (borrow ? 10 : 0));
        int i;
        for (i = 1; i < Math.min(numberOfDigits(), arg.numberOfDigits()); i++) {
            /// for loop in here (what if you need to borrow multiple times
            for (int j = 0; j < numberOfDigits(); j++) {
                // TODO: there's a lot to do here, pick up a pen and paper
            }
            borrow = checkUnderflow(arg, i);
            result.addADigit(difference(arg, borrow, i));
        }
        return result;
    }

    private int[] addDiagonals(int[][] lattice) {
        int height = lattice.length;
        int width = lattice[0].length;
        int diagonals = width + height - 1;
        int[] result = new int[diagonals];
        int diag = diagonals - 1;

        for (int col = width - 1; col >= 0; col--) {
            result[diag] = diagonalSum(lattice, col, height - 1);
            diag--;
        }
        for (int row = height - 2; row >= 0; row--) {
            result[diag] = diagonalSum(lattice, 0, row);
            diag--;
        }
        return result;

    }

    private int diagonalSum(int[][] lattice, int row, int column) { // flipped
        int sum = 0;
        sum += lattice[column][row];
        while (row + 1 <= lattice[0].length - 1 && column - 1 >= 0) {
            sum += lattice[--column][++row];
        }
        return sum;
    }

    public BCD multiplyBCDs(BCD other) {
        int[][] lattice = new int[numberOfDigits()][other.numberOfDigits()];
        int remainder = 0;
        int digit;
        if ((numberOfDigits() == 1 && nthDigit(0) == 0) || other.numberOfDigits() == 1 && other.nthDigit(0) == 0) {
            return new BCD(0);
        } else {
            for (int i = 0; i < lattice.length; i++) {
                for (int j = 0; j < lattice[0].length; j++) {
                    lattice[i][j] = nthDigit(i) * other.nthDigit(j);
                }
            }
            int[] diagonals = addDiagonals(lattice);
            digit = diagonals[0];
            remainder = digit;
            digit %= 10;
            remainder /= 10;
            BCD product = new BCD(digit);
            for (int i = 1; i < diagonals.length; i++) {
                digit = diagonals[i] + remainder;
                remainder = digit;
                digit %= 10;
                remainder /= 10;
                product.addADigit(digit);
            }
            if (remainder != 0) {
                product.addADigit(remainder);
            }
            return product;
        }
    }

    public static BCD factorial(int num) {
        if (num == 0) {
            return new BCD(1);
        }
        BCD factorial = new BCD(num);
        for (int i = 2; i < num; i++) {
            BCD index = new BCD(i);
            factorial = factorial.multiplyBCDs(index);
        }
        return factorial;
    }

    public BCD pow(int num) {
        BCD power = new BCD(digits);
        BCD sum = new BCD(1);
        for (int i = 0; i < num; i++) {
            sum = sum.multiplyBCDs(power);
        }
        return sum;
    }

    public int[] getDigits() {
        int[] digitList = new int[numberOfDigits()];
        for (int i = 0; i < digitList.length; i++) {
            digitList[i] = nthDigit(i);
        }
        return digitList;
    }

    public boolean greater(BCD arg) {
        if (numberOfDigits() > arg.numberOfDigits()) {
            return true;
        } else if (numberOfDigits() < arg.numberOfDigits()) {
            return false;
        } else {
            int[] digits1 = getDigits();
            int[] digits2 = arg.getDigits();
            for (int i = 0; i < numberOfDigits(); i++) {
                if (digits1[i] < digits2[i]) {
                    return false;
                }
            }
            if (digits1[digits1.length - 1] == digits2[digits2.length - 1]) {
                return false;
            }
            return true;
        }
    }

    public boolean less(BCD arg) {
        return arg.greater(this);
    }

    public boolean equals(BCD arg) {
        if (arg.numberOfDigits() != numberOfDigits()) {
            return false;
        }
        int[] argDigits = arg.getDigits();
        for (int i = 0; i < numberOfDigits(); i++) {
            if (digits[i] != argDigits[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean notEqual(BCD arg) {
        return !this.equals(arg);
    }

    public String toString() {
        String digits = "";
        int[] nums = getDigits();
        for (int i = nums.length - 1; i >= 0; i--) {
            digits += nums[i];
            if (i % 3 == 0 && i - 1 > 0) {
                digits += ',';
            }
        }

        return digits;
    }

    public static void testCases() {
        int[] zero = { 0 };
        int[] oneDigit = { 7 };
        int[] twoDigit = { 2, 5 };
        int[] fourDigit = { 5, 3, 2, 1 };

        BCD a = new BCD(zero);
        BCD b = new BCD(oneDigit);
        BCD c = new BCD(twoDigit);
        BCD d = new BCD(fourDigit);

        BCD[] bcds = { a, b, c, d };

        int addedDigit = 1;
        for (int i = 0; i < bcds.length; i++) {
            if (i < 2) {
                System.out.print(i == 0 ? "Zero: " : "One digit: ");
            } else {
                System.out.print(i == 2 ? "Two digits: " : "More than 2 digits: ");
            }
            System.out.println(bcds[i].toString());

            if (bcds[i].numberOfDigits() >= 1) {
                System.out.print("nthDigit where n = 0: ");
                System.out.println(bcds[i].nthDigit(0));
            }
            if (2 < bcds[i].numberOfDigits()) {
                System.out.print("nthDigit where n + 1 < numDigits and > 1: ");
                System.out.println(bcds[i].nthDigit(2));
            }
            bcds[i].nthDigit(bcds[i].numberOfDigits() - 1);
            System.out.print("n > numDigits: ");
            System.out.println(bcds[i].nthDigit(bcds[i].numberOfDigits() + 1));

            System.out.println("adding digit " + addedDigit + " to " + bcds[i].toString());
            bcds[i].addADigit(addedDigit);
            System.out.println("returns: " + bcds[i].toString());
        }
    }

    public static void testNew() {
        BCD[] bcds = { new BCD(0), new BCD(7), new BCD(123), new BCD(1234), new BCD(123456), new BCD(12345678) };
        for (BCD x : bcds) {
            System.out.println(x);
        }
    }

    public static void additionTests() {

        BCD b1 = new BCD(0), b2 = new BCD(0);

        BCD b3 = new BCD(1), b4 = new BCD(0);

        BCD b5 = new BCD(0), b6 = new BCD(1);

        BCD b7 = new BCD(100), b8 = new BCD(33);

        BCD b9 = new BCD(99), b10 = new BCD(2);

        BCD b11 = new BCD(2), b12 = new BCD(99);

        BCD b13 = new BCD(99), b14 = new BCD(11);
        BCD[] bcds = { b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14 };
        for (int i = 0; i < bcds.length; i += 2) {
            System.out.println("Base: " + bcds[i] + "\nParameter: " + bcds[i + 1]);
            System.out.println("Returns: " + bcds[i].addBCDs(bcds[i + 1]));
        }
    }

    public static void multiplicationTests() {
        BCD b15 = new BCD(4857), b16 = new BCD(329);

        BCD b17 = new BCD(3), b18 = new BCD(22);

        BCD b19 = new BCD(5), b20 = new BCD(4);

        BCD b21 = new BCD(0), b22 = new BCD(7);

        BCD b23 = new BCD(1234056), b24 = new BCD(6540321);

        BCD b25 = new BCD(10000), b26 = new BCD(12345);

        BCD[] bcds = { b15, b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26 };
        for (int i = 0; i < bcds.length; i += 2) {
            System.out.println(bcds[i] + " * " + bcds[i + 1] + ": ");
            System.out.println("returns: " + bcds[i].multiplyBCDs(bcds[i + 1]));
        }
    }

    public static void powerTests() {
        BCD f1 = new BCD(52), f2 = new BCD(104);

        BCD p1 = new BCD(2), p2 = new BCD(19);

        System.out.println(f1 + "!=\n" + factorial(52));
        System.out.println(f2 + "!=\n" + factorial(104));

        System.out.println("\n" + p1 + "^127=\n" + p1.pow(127));
        System.out.println(p2 + "^19=\n" + p2.pow(19));

    }

    public static void main(String[] args) {
        // testCases();
        // testNew();
        // additionTests();
        // multiplicationTests();
        // powerTests();

        BCD a = new BCD(0);
        a.addADigit(1);
        a.addADigit(2);
        System.out.println(a.toString());
    }
}
