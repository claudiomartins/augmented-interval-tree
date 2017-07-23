

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {
	
	private static final String TIME12HOURS_PATTERN = "^((([0]?[1-9]|1[0-2])(:|\\.)([0-5][0-9])((:|\\.)[0-5][0-9])?( )?(AM|am|aM|Am|PM|pm|pM|Pm))|(([0]?[0-9]|1[0-9]|2[0-3])(:|\\.)[0-5][0-9]((:|\\.)[0-5][0-9])?)),.?((([0]?[1-9]|1[0-2])(:|\\.)([0-5][0-9])((:|\\.)[0-5][0-9])?( )?(AM|am|aM|Am|PM|pm|pM|Pm))|(([0]?[0-9]|1[0-9]|2[0-3])(:|\\.)[0-5][0-9]((:|\\.)[0-5][0-9])?))$";

	public static void main(String[] args) {

		Pattern pattern = Pattern.compile(TIME12HOURS_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		BikesAtIntervalIntree bikeTree = new BikesAtIntervalIntree();

		Set<Long> uniqueDates = new TreeSet<>();

		Scanner scanner = new Scanner(System.in);

		System.out.println(
				"### please input dates in this format: '7:13 AM, 7:23 AM' (without the single quotes) - leave one line empty to process the results");

		while (true) {

			String typedInput = scanner.nextLine();

			if (typedInput == null || typedInput.trim().equals("")) {
				System.out.println("### end of input - processing results now!");
				scanner.close();
				break;
			} else {

				String[] lines = typedInput.split(System.getProperty("line.separator"));

				for (String line : lines) {
					Interval interval = addIntervalFromInput(bikeTree, pattern, line);
					uniqueDates.add(interval.getStart());
					uniqueDates.add(interval.getEnd());
				}

			}

		}

		printDateRanges(uniqueDates, bikeTree);

	}

	private static void printDateRanges(Set<Long> uniqueDates, BikesAtIntervalIntree bikeTree) {

		Long previousDate = null;

		Iterator<Long> uniqueDatesIterator = uniqueDates.iterator();

		while (uniqueDatesIterator.hasNext()) {

			Long currentDate = uniqueDatesIterator.next();

			Long startDate = currentDate;
			Long endDate = currentDate;

			if (previousDate == null) {
				endDate = uniqueDatesIterator.next();
			} else {
				startDate = previousDate;
				endDate = currentDate;
			}

			int totalFound = bikeTree.findIntervals(new Interval(startDate + 1000, endDate - 1000L));

			System.out.println(new SimpleDateFormat("HH:mm aa").format(new Date(startDate)) + ", "
					+ new SimpleDateFormat("HH:mm aa").format(new Date(endDate)) + ": " + totalFound);

			previousDate = endDate;

		}

	}

	private static Interval addIntervalFromInput(BikesAtIntervalIntree bikeTree, Pattern pattern, String line) {

		Interval interval = null;

		Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {

			Integer hour1 = Integer.valueOf(matcher.group(3));
			Integer minute1 = Integer.valueOf(matcher.group(5));
			String ampm1 = matcher.group(9);

			Integer hour2 = Integer.valueOf(matcher.group(17));
			Integer minute2 = Integer.valueOf(matcher.group(19));
			String ampm2 = matcher.group(23);

			Calendar intervalStart = GregorianCalendar.getInstance();
			Calendar intervalEnd = GregorianCalendar.getInstance();

			intervalStart.set(Calendar.AM_PM, ampm1.equalsIgnoreCase("AM") ? Calendar.AM : Calendar.PM);
			intervalStart.set(Calendar.HOUR, hour1);
			intervalStart.set(Calendar.MINUTE, minute1);
			intervalStart.set(Calendar.SECOND, 0);
			intervalStart.set(Calendar.MILLISECOND, 0);

			intervalEnd.set(Calendar.AM_PM, ampm2.equalsIgnoreCase("AM") ? Calendar.AM : Calendar.PM);
			intervalEnd.set(Calendar.HOUR, hour2);
			intervalEnd.set(Calendar.MINUTE, minute2);
			intervalEnd.set(Calendar.SECOND, 0);
			intervalEnd.set(Calendar.MILLISECOND, 0);

			interval = new Interval(intervalStart.getTimeInMillis(), intervalEnd.getTimeInMillis());

			bikeTree.insertInterval(interval);

		} else {
			System.out.println("### invalid input: " + line);
		}

		return interval;

	}

}

class BikesAtIntervalIntree {

	private Interval root;

	public void insertInterval(Interval newNode) {

		if (root == null) {
			root = newNode;
			return;
		} else {
			insertInterval(root, newNode);
		}

	}

	private void insertInterval(Interval currentIntervalNode, Interval newInterval) {

		if (currentIntervalNode == null) {
			return;
		}

		if (newInterval.getEnd() > currentIntervalNode.getMax()) {
			currentIntervalNode.setMax(newInterval.getEnd());
		}

		if (currentIntervalNode.compareTo(newInterval) <= 0) {

			if (currentIntervalNode.getRight() == null) {
				currentIntervalNode.setRight(newInterval);
			} else {
				insertInterval(currentIntervalNode.getRight(), newInterval);
			}
		} else {
			if (currentIntervalNode.getLeft() == null) {
				currentIntervalNode.setLeft(newInterval);
			} else {
				insertInterval(currentIntervalNode.getLeft(), newInterval);
			}
		}

	}

	public int findIntervals(Interval intervalToFind) {

		if (root == null) {
			return 0;
		}

		List<Interval> foundIntervals = findIntervals(root, intervalToFind);

		return foundIntervals != null ? foundIntervals.size() : 0;

	}

	private List<Interval> findIntervals(Interval starterNode, Interval intervalToFind) {

		List<Interval> foundIntervals = new ArrayList<>();

		if (starterNode == null) {
			return foundIntervals;
		}

		if (!((starterNode.getStart() > intervalToFind.getEnd())
				|| (starterNode.getEnd() < intervalToFind.getStart()))) {
			foundIntervals.add(starterNode);
		}

		if ((starterNode.getLeft() != null) && (starterNode.getLeft().getMax() >= intervalToFind.getStart())) {
			foundIntervals.addAll(this.findIntervals(starterNode.getLeft(), intervalToFind));
		}

		foundIntervals.addAll(this.findIntervals(starterNode.getRight(), intervalToFind));

		return foundIntervals;

	}

}

class Interval implements Comparable<Interval> {

	private long start;
	private long end;
	private long max;
	private Interval left;
	private Interval right;

	public Interval(long start, long end) {
		this.start = start;
		this.end = end;
		this.max = end;
	}

	public long getStart() {
		return this.start;
	}

	public long getEnd() {
		return this.end;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public Interval getLeft() {
		return left;
	}

	public void setLeft(Interval left) {
		this.left = left;
	}

	public Interval getRight() {
		return right;
	}

	public void setRight(Interval right) {
		this.right = right;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Interval [start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append(", max=");
		builder.append(max);
		builder.append(", left=");
		builder.append(left);
		builder.append(", right=");
		builder.append(right);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(Interval i) {
		if (this.start < i.start) {
			return -1;
		} else if (this.start == i.start) {
			return this.end <= i.end ? -1 : 1;
		} else {
			return 1;
		}
	}

}
