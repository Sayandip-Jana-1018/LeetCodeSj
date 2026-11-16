# 2243 · Calculate Digit Sum of a String

**Platform:** LeetCode (Daily Question, 16 Nov 2026) · **Difficulty:** Easy · **Tags:** String, Simulation · [Original problem](https://leetcode.com/problems/calculate-digit-sum-of-a-string/)

---

## 1. The problem in simple words

You get a string `s` made only of digits, and a number `k`.

As long as `s` is **longer than `k`**, repeat one round:

1. Cut `s` into pieces of `k` characters, left to right. The last piece may be shorter.
2. Replace each piece with the **sum of its digits**, written as a number (for example, `"346"` becomes `"13"`).
3. Glue the pieces back together. That's the new `s`.

Return `s` when its length is `k` or less.

| Limit | Value |
|---|---|
| `s.length()` | 1 to 100 |
| `k` | 2 to 100 |
| `s` | digits only |

---

## 2. Intuition

There's no trick here: the problem tells you exactly what to do, so you **simulate it**. The two things to get right:

- A piece's sum can have **two or more digits** (`9 + 9 + 9 = 27`), so you append the whole number, not a single character.
- The loop condition is **"length > k"**, not "≥".

The string keeps shrinking. Each piece of `k ≥ 2` digits turns into a shorter number, so the loop always ends quickly.

---

## 3. Brute force vs optimal

| | Brute force | Optimal |
|---|---|---|
| How | Each round, use `substring` for every piece and `String +=` to build the next string | Each round, sum digits with an index loop and build with a `StringBuilder` |
| Cost per round | Up to O(n²), since `+=` copies the whole string each time | O(n) |
| Verdict | Fine for n ≤ 100, but a bad habit | ✅ Clean and linear |

For this problem both pass, but `StringBuilder` is the habit you want for bigger strings.

---

## 4. Optimal approach

```
while s.length() > k:
    next = empty builder
    for start = 0, k, 2k, ...:
        sum the digits from start to min(start + k, n) - 1
        append sum to next
    s = next
return s
```

---

## 5. Dry run

**Input:** `s = "11111222223"`, `k = 3`

**Round 1** (length 11 > 3):

| Piece | Digit sum |
|---|---|
| `111` | 3 |
| `112` | 4 |
| `222` | 6 |
| `23` (short last piece) | 5 |

New `s = "3465"` (length 4 > 3, so keep going)

**Round 2:**

| Piece | Digit sum |
|---|---|
| `346` | 13 ← two digits! |
| `5` | 5 |

New `s = "135"` (length 3, not > 3, so stop)

**Output:** `"135"` ✅

**Second example:** `s = "00000000"`, `k = 3` → pieces `000`, `000`, `00` → sums `0`, `0`, `0` → `"000"` (length 3, stop) → **`"000"`**

---

## 6. Java code

```java
class Solution {
    public String digitSum(String s, int k) {
        while (s.length() > k) {
            StringBuilder next = new StringBuilder();
            for (int i = 0; i < s.length(); i += k) {
                int sum = 0;
                int end = Math.min(i + k, s.length());
                for (int j = i; j < end; j++) sum += s.charAt(j) - '0';
                next.append(sum);
            }
            s = next.toString();
        }
        return s;
    }
}
```

The same code is in [`solution.java`](./solution.java).

---

## 7. Complexity

| | Cost | Why |
|---|---|---|
| **Time** | O(n) per round, and only a few rounds | The string shrinks every round, so total work stays close to O(n) |
| **Space** | **O(n)** | The `StringBuilder` for the next string |

---

## 8. Edge cases

- `s.length() ≤ k` from the start: return `s` unchanged.
- A piece of all zeros sums to `0`, written as `"0"`.
- The last piece is shorter than `k`. That's handled by `Math.min(i + k, n)`.

## 9. Common mistakes

- Writing `while (s.length() >= k)`. It should be strictly `>`.
- Appending `(char) (sum + '0')`, which breaks when the sum is 10 or more. Append the int instead.
- Forgetting that the last piece can be shorter.

## 10. Key takeaway

> "Repeat until" problems are often pure simulation. Get the loop condition and the boundary (the last, shorter piece) right, and use a `StringBuilder` for string building.

---

## Submission

**Accepted** · 121/121 tests · 1 ms (beats 98.69%) · 42.75 MB · Java · submitted twice (both Accepted)
