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
