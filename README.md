# Case

You are given a list of bike rides with start/end time, print out a timeline summary of how many

concurrent rides are happening at a given time.

The input is a list of rides with start / end time exactly like following (from standard input)

7:13 AM, 7:23 AM
6:50 AM, 7:08 AM
7:10 AM, 7:30 AM
6:52 AM, 7:33 AM
6:58 AM, 7:23 AM

The exact output for this list of input should be (to the standard output)

6:50 AM, 6:52 AM, 1
6:52 AM, 6:58 AM, 2
6:58 AM, 7:08 AM, 3
7:08 AM, 7:10 AM, 2
7:10 AM, 7:13 AM, 3
7:13 AM, 7:23 AM, 4
7:23 AM, 7:30 AM, 2
7:30 AM, 7:33 AM, 1

Note that the time intervals here are continuous and showing exactly how many trips at that time interval.

The code you produced should be compile after directly pasting in codepad.io and take the input to generate the output.


# Solution

The data structure chosen is the Augmented Interval Tree as it is optimized to query which intervals overlaps with a given interval, while a Segment Tree is optimized to query the intervals that contain a certain point.

An Augmented Interval Tree has:

- O(n log n) preprocessing time
- O(k + log n) query time
- O(n) space



While a Segment Tree has:

- O(n log n) preprocessing time
- O(k + log n) query time
- O(n log n) space

_k_ = number of reported intervals

Both have O(log n) time for insertion/deletion.

_n_ being the total number of intervals in the tree prior to the insertion or deletion operation.


Each node contains the information about the maximum value of the subtree, this comes handy when analyzing the tree structure as we can avoid comparisons when trying to find the overlapping intervals.


# What this code is not doing

- deletion of nodes
- recalculating the maximum value of the subtree when a change or deletion happens in any of the nodes


