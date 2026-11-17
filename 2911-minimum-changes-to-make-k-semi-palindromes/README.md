# 2911 · Minimum Changes to Make K Semi-palindromes

**Platform:** LeetCode (Daily Question, 17 Nov 2026) · **Difficulty:** Hard · **Tags:** String, Two Pointers, Dynamic Programming · [Original problem](https://leetcode.com/problems/minimum-changes-to-make-k-semi-palindromes/)

---

## 1. The problem in simple words

You get a lowercase string `s` and a number `k`. Cut `s` into exactly `k` non-empty pieces, left to right. Then fix each piece by changing as few letters as possible, so that every piece becomes a **semi-palindrome**. Return the smallest total number of changes.

**What is a semi-palindrome?** Take a piece of length `len`. Pick a step `d` where `1 ≤ d < len` and `d` divides `len`. Split the piece into `d` groups by position modulo `d`: positions `0, d, 2d, …` form group 0, positions `1, 1+d, 1+2d, …` form group 1, and so on. If **every group reads the same forwards and backwards** for at least one valid `d`, the piece is a semi-palindrome.

- `d = 1` means the whole piece must be a normal palindrome.
- A piece of length 1 has no valid `d`, so it can **never** be a semi-palindrome. Every piece needs length ≥ 2.

Example: `"adbgad"` with `d = 2` gives groups `a b a` and `d g d`. Both are palindromes, so it's a semi-palindrome with 0 changes.

| Limit | Value |
|---|---|
| `s.length()` | 2 to 200 |
| `k` | 1 to `s.length() / 2` |
| `s` | lowercase English letters |

---

## 2. Intuition

The problem splits into two independent parts:

1. **Cost of one piece.** For a fixed piece and a fixed `d`, each group is fixed on its own. To make a group a palindrome, compare it from both ends: every mismatched pair needs exactly one change (change either letter to match the other). The piece's cost is the **minimum over all valid `d`**.
2. **Best way to cut.** Once every substring has a known cost, picking `k` consecutive pieces with minimum total cost is the classic **"split an array into k parts" DP**.

`n ≤ 200` is small. That's a hint we can afford to precompute the cost of **every** substring.

---

## 3. Brute force vs optimal

**Brute force:** try every way to place `k − 1` cut points (that's `C(n−1, k−1)` ways, which is huge), and for each piece compute its cost from scratch.
With `n = 200, k = 100` the number of cuttings is astronomical, so this is impossible.

**Optimal:** compute `cost[l][r]` once for all `O(n²)` substrings, then run a DP over (number of pieces, prefix length). This reuses work instead of recomputing the same pieces again and again.

---

## 4. Optimal approach

1. **Divisors.** For every length `len`, list each `d` with `d < len` and `len % d == 0`. (Loop `d` and add it to `len = 2d, 3d, …`.)
2. **Cost table.** For every substring `s[l..r]` (length ≥ 2), and each divisor `d`:
   - group size `m = len / d`;
   - for each group `g` in `0..d−1`, compare position `a` with `m−1−a` inside the group, i.e. `s[l+g+a·d]` with `s[l+g+(m−1−a)·d]`, and count mismatches;
   - `cost[l][r] = min` over all `d`.
3. **DP.** `dp[p][i]` = minimum changes to split the first `i` characters into `p` pieces.
   - `dp[0][0] = 0`, everything else starts as infinity.
   - `dp[p][i] = min over j of dp[p−1][j] + cost[j][i−1]`, where the last piece is `s[j..i−1]` and has length ≥ 2.
4. Answer: `dp[k][n]`.

---

## 5. Dry run

`s = "abcac"`, `k = 2`.

**Costs of the pieces we need** (`d` = 1 means "make it a palindrome"):

| Piece | Valid `d` | Mismatches | Cost |
|---|---|---|---|
| `ab` | 1 | a≠b | 1 |
| `abc` | 1 | a≠c | 1 |
| `abca` | 1, 2 | d=1: b≠c → 1 · d=2: groups `ac`, `ba` → 2 | 1 |
| `cac` | 1 | none | 0 |
| `ac` | 1 | a≠c | 1 |
| `bcac` | 1, 2 | d=1: 2 · d=2: groups `ba`, `cc` → 1 | 1 |

**DP for 2 pieces ending at `i = 5`:**

| First piece | Second piece | `dp[1][j]` | `cost` | Total |
|---|---|---|---|---|
| `ab` (j=2) | `cac` | 1 | 0 | **1** |
| `abc` (j=3) | `ac` | 1 | 1 | 2 |

A cut at `j = 4` would leave a 1-letter piece, which is not allowed.
**Answer: 1.** Cut into `"ab" + "cac"` and change one letter of `"ab"`.

---

## 6. Java code

```java
class Solution {
    public int minimumChanges(String s, int k) {
        int n = s.length();
        char[] c = s.toCharArray();

        // divisors[len] = every d with 1 <= d < len and len % d == 0
        java.util.List<Integer>[] divs = new java.util.ArrayList[n + 1];
        for (int len = 0; len <= n; len++) divs[len] = new java.util.ArrayList<>();
        for (int d = 1; d <= n; d++)
            for (int len = 2 * d; len <= n; len += d) divs[len].add(d);

        // cost[l][r] = fewest changes to turn c[l..r] into a semi-palindrome
        int[][] cost = new int[n][n];
        for (int l = 0; l < n; l++) {
            for (int r = l + 1; r < n; r++) {
                int len = r - l + 1, best = Integer.MAX_VALUE;
                for (int d : divs[len]) {
                    int m = len / d, changes = 0;
                    for (int g = 0; g < d; g++)                 // group g: l+g, l+g+d, ...
                        for (int a = 0, b = m - 1; a < b; a++, b--)
                            if (c[l + g + a * d] != c[l + g + b * d]) changes++;
                    best = Math.min(best, changes);
                }
                cost[l][r] = best;
            }
        }

        // dp[p][i] = min changes to split the first i chars into p parts
        final int INF = 1_000_000;
        int[][] dp = new int[k + 1][n + 1];
        for (int[] row : dp) java.util.Arrays.fill(row, INF);
        dp[0][0] = 0;
        for (int p = 1; p <= k; p++)
            for (int i = 2 * p; i <= n; i++)                  // every part has length >= 2
                for (int j = 2 * (p - 1); j <= i - 2; j++)
                    if (dp[p - 1][j] < INF)
                        dp[p][i] = Math.min(dp[p][i], dp[p - 1][j] + cost[j][i - 1]);
        return dp[k][n];
    }
}
```

The same code is in [`solution.java`](./solution.java).

---

## 7. Complexity

| | Value | Why |
|---|---|---|
| Time (cost table) | `O(n³ · D)`, where `D` is the (small) number of divisors of a length | `O(n²)` substrings × `D` divisors × `O(len)` comparisons each |
| Time (DP) | `O(k · n²)` | `k` layers, each tries every `(i, j)` pair |
| Space | `O(n² + k·n)` | The cost table plus the DP table |

With `n = 200` this is a few million steps (about 40 ms locally for the max case).

---

## 8. Edge cases

- **Pieces of length 1:** never valid. The DP enforces it with `j ≤ i − 2` and starts `i` at `2p`.
- **Prime length:** only `d = 1` is allowed, so the piece must be a full palindrome.
- **`k = n / 2`:** every piece has length exactly 2, and each costs 1 unless both letters match.
- **`k = 1`:** the answer is simply `cost[0][n−1]`.

---

## 9. Common mistakes

- Allowing `d = len`: that would make every string trivially "semi-palindromic". `d` must be **strictly less** than `len`.
- Counting both letters of a mismatched pair (2 changes) instead of 1.
- Letting the DP use 1-letter pieces, which gives answers that are too small.
- Recomputing a piece's cost inside the DP loop. That's correct but much slower; precompute it.

---

## 10. Key takeaway

> Split "cost of one segment" from "best way to cut". Precompute every segment's cost, then the cut is the standard `dp[parts][prefix]` partition DP.

---

## Submission

**Accepted** · 110 ms · 46.8 MB · Java · submitted twice (both Accepted)
