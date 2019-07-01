#SQL 1
#Top 5 successful USD campaigns compare to unsuccessful
SELECT
  *
FROM
  (
    SELECT
      cat.NAME AS CategoryName,
      ROUND(AVG(c.goal), 2) AS AvgGoal,
      ROUND(AVG(c.pledged), 2) AS AvgPledged,
      ROUND(
        (
          ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
        ),
        2
      ) AS AvgDonation,
      ROUND(AVG(c.backers), 0) AS AvgDonators,
      ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
      ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
      COUNT(*) AS Entries
    FROM
      campaign AS c
      JOIN currency AS cu ON c.currency_id = cu.id
      JOIN sub_category AS scat ON c.sub_category_id = scat.id
      JOIN category AS cat ON scat.category_id = cat.id
    WHERE
      NOT(c.outcome = 'undefined')
      AND (c.backers > 0)
      AND(cu.NAME = 'USD')
      AND (c.outcome = 'successful')
    GROUP BY
      cat.NAME
    HAVING
      AvgPledged > (15000 * 0.05 + 15000) #5% KickStarter Cut
    ORDER BY
      AvgPledged DESC
    LIMIT
      5
  ) AS AliasOne
UNION
  (
    SELECT
      cat.NAME AS CategoryName,
      ROUND(AVG(c.goal), 2) AS AvgGoal,
      ROUND(AVG(c.pledged), 2) AS AvgPledged,
      ROUND(
        (
          ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
        ),
        2
      ) AS AvgDonation,
      ROUND(AVG(c.backers), 0) AS AvgDonators,
      ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
      ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
      COUNT(*) AS Entries
    FROM
      campaign AS c
      JOIN currency AS cu ON c.currency_id = cu.id
      JOIN sub_category AS scat ON c.sub_category_id = scat.id
      JOIN category AS cat ON scat.category_id = cat.id
    WHERE
      NOT (
        c.outcome = 'undefined'
        OR c.outcome = 'successful'
        OR c.outcome = 'live'
      )
      AND (
        scat.NAME = 'Technology'
        OR scat.NAME = 'Games'
        OR scat.NAME = 'Design'
        OR scat.NAME = 'Fashion'
        OR scat.NAME = 'Film & Video'
      )
      AND (c.backers > 0)
      AND (cu.NAME = 'USD')
    GROUP BY
      cat.NAME
  )
ORDER BY
  CategoryName ASC,
  SuccessScore DESC;

#SQL 2
SELECT
  scat.name AS SubCategory,
  cat.NAME AS CategoryName,
  c.outcome AS Outcome,
  ROUND(AVG(c.goal), 2) AS AvgGoal,
  ROUND(AVG(c.pledged), 2) AS AvgPledged,
  ROUND(
    (
      ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
    ),
    2
  ) AS AvgDonation,
  ROUND(AVG(c.backers), 0) AS AvgDonators,
  ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
  ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
  COUNT(*) AS Entries
FROM
  campaign AS c
  JOIN currency AS cu ON c.currency_id = cu.id
  JOIN sub_category AS scat ON c.sub_category_id = scat.id
  JOIN category AS cat ON scat.category_id = cat.id
WHERE
  NOT(c.outcome = 'undefined')
  AND (c.backers > 0)
  AND (cu.NAME = 'USD')
  AND (c.outcome = 'successful')
GROUP BY
  c.sub_category_id,
  cat.NAME,
  c.outcome
HAVING
  COUNT(*) > 4 #At least 5 projects
  AND AvgPledged > (15000 * 0.05 + 15000) #5% KickStarter Cut
  AND AvgGoal > (15000 * 0.66) #Goal is at least 66% from expected 15000
ORDER BY
  SuccessScore DESC
LIMIT
  20;

#SQL 3
SELECT
  scat.name AS SubCategory,
  cat.NAME AS CategoryName,
  c.outcome AS Outcome,
  ROUND(AVG(c.goal), 2) AS AvgGoal,
  ROUND(AVG(c.pledged), 2) AS AvgPledged,
  ROUND(
    (
      ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
    ),
    2
  ) AS AvgDonation,
  ROUND(AVG(c.backers), 0) AS AvgDonators,
  ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
  ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
  COUNT(*) AS Entries
FROM
  campaign AS c
  JOIN currency AS cu ON c.currency_id = cu.id
  JOIN sub_category AS scat ON c.sub_category_id = scat.id
  JOIN category AS cat ON scat.category_id = cat.id
WHERE
  NOT(c.outcome = 'undefined')
  AND (c.backers > 0)
  AND (cu.NAME = 'USD')
  AND (c.outcome = 'successful')
  AND (c.launched < '2014-01-01')
GROUP BY
  c.sub_category_id,
  cat.NAME,
  c.outcome
HAVING
  COUNT(*) > 4 #At least 5 projects
  AND AvgPledged > (15000 * 0.05 + 15000) #5% KickStarter Cut
  AND AvgGoal > (15000 * 0.66) #Goal is at least 66% from expected 15000
  AND SuccessScore > 5
ORDER BY
  SuccessScore DESC
LIMIT
  20;

#SQL 4
SELECT
  scat.name AS SubCategory,
  cat.NAME AS CategoryName,
  c.outcome AS Outcome,
  ROUND(AVG(c.goal), 2) AS AvgGoal,
  ROUND(AVG(c.pledged), 2) AS AvgPledged,
  ROUND(
    (
      ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
    ),
    2
  ) AS AvgDonation,
  ROUND(AVG(c.backers), 0) AS AvgDonators,
  ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
  ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
  COUNT(*) AS Entries
FROM
  campaign AS c
  JOIN currency AS cu ON c.currency_id = cu.id
  JOIN sub_category AS scat ON c.sub_category_id = scat.id
  JOIN category AS cat ON scat.category_id = cat.id
WHERE
  NOT(c.outcome = 'undefined')
  AND (c.backers > 0)
  AND (cu.NAME = 'USD')
  AND (c.outcome = 'successful')
  AND (c.launched >= '2014-01-01')
GROUP BY
  c.sub_category_id,
  cat.NAME,
  c.outcome
HAVING
  COUNT(*) > 4 #At least 5 projects
  AND AvgPledged > (15000 * 0.05 + 15000) #5% KickStarter Cut
  AND AvgGoal > (15000 * 0.66) #Goal is at least 66% from expected 15000
  AND SuccessScore > 5
ORDER BY
  SuccessScore DESC
LIMIT
  20;


#SQL 5
(
  SELECT
    scat.name AS SubCategory,
    cat.NAME AS CategoryName,
    ROUND(AVG(c.goal), 2) AS AvgGoal,
    ROUND(AVG(c.pledged), 2) AS AvgPledged,
    ROUND(
      (
        ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
      ),
      2
    ) AS AvgDonation,
    ROUND(AVG(c.backers), 0) AS AvgDonators,
    ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
    ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
    COUNT(*) AS Entries
  FROM
    campaign AS c
    JOIN currency AS cu ON c.currency_id = cu.id
    JOIN sub_category AS scat ON c.sub_category_id = scat.id
    JOIN category AS cat ON scat.category_id = cat.id
  WHERE
    NOT(c.outcome = 'undefined')
    AND (c.backers > 0)
    AND (cu.NAME = 'USD')
    AND (c.outcome = 'successful')
    AND (c.launched < '2014-01-01')
  GROUP BY
    c.sub_category_id,
    cat.NAME
  HAVING
    COUNT(*) > 4 #At least 5 projects
    AND (SubCategory = 'Tabletop Games')
  ORDER BY
    SuccessScore DESC
  LIMIT
    20
)
UNION(
    SELECT
      scat.name AS SubCategory,
      cat.NAME AS CategoryName,
      ROUND(AVG(c.goal), 2) AS AvgGoal,
      ROUND(AVG(c.pledged), 2) AS AvgPledged,
      ROUND(
        (
          ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
        ),
        2
      ) AS AvgDonation,
      ROUND(AVG(c.backers), 0) AS AvgDonators,
      ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
      ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
      COUNT(*) AS Entries
    FROM
      campaign AS c
      JOIN currency AS cu ON c.currency_id = cu.id
      JOIN sub_category AS scat ON c.sub_category_id = scat.id
      JOIN category AS cat ON scat.category_id = cat.id
    WHERE
      NOT (
        c.outcome = 'undefined'
        OR c.outcome = 'successful'
        OR c.outcome = 'live'
      )
      AND (c.backers > 0)
      AND (cu.NAME = 'USD')
      AND (c.launched < '2014-01-01')
    GROUP BY
      c.sub_category_id,
      cat.NAME
    HAVING
      COUNT(*) > 4 #At least 5 projects
      AND (SubCategory = 'Tabletop Games')
    ORDER BY
      SuccessScore DESC
    LIMIT
      20
  )

#SQL6
(
  SELECT
    scat.name AS SubCategory,
    cat.NAME AS CategoryName,
    ROUND(AVG(c.goal), 2) AS AvgGoal,
    ROUND(AVG(c.pledged), 2) AS AvgPledged,
    ROUND(
      (
        ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
      ),
      2
    ) AS AvgDonation,
    ROUND(AVG(c.backers), 0) AS AvgDonators,
    ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
    ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
    COUNT(*) AS Entries
  FROM
    campaign AS c
    JOIN currency AS cu ON c.currency_id = cu.id
    JOIN sub_category AS scat ON c.sub_category_id = scat.id
    JOIN category AS cat ON scat.category_id = cat.id
  WHERE
    NOT(c.outcome = 'undefined')
    AND (c.backers > 0)
    AND (cu.NAME = 'USD')
    AND (c.outcome = 'successful')
    AND (c.launched >= '2014-01-01')
  GROUP BY
    c.sub_category_id,
    cat.NAME
  HAVING
    COUNT(*) > 4 #At least 5 projects
    AND (SubCategory = 'Tabletop Games')
  ORDER BY
    SuccessScore DESC
  LIMIT
    20
)
UNION(
    SELECT
      scat.name AS SubCategory,
      cat.NAME AS CategoryName,
      ROUND(AVG(c.goal), 2) AS AvgGoal,
      ROUND(AVG(c.pledged), 2) AS AvgPledged,
      ROUND(
        (
          ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
        ),
        2
      ) AS AvgDonation,
      ROUND(AVG(c.backers), 0) AS AvgDonators,
      ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
      ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
      COUNT(*) AS Entries
    FROM
      campaign AS c
      JOIN currency AS cu ON c.currency_id = cu.id
      JOIN sub_category AS scat ON c.sub_category_id = scat.id
      JOIN category AS cat ON scat.category_id = cat.id
    WHERE
      NOT (
        c.outcome = 'undefined'
        OR c.outcome = 'successful'
        OR c.outcome = 'live'
      )
      AND (c.backers > 0)
      AND (cu.NAME = 'USD')
      AND (c.launched >= '2014-01-01')
    GROUP BY
      c.sub_category_id,
      cat.NAME
    HAVING
      COUNT(*) > 4 #At least 5 projects
      AND (SubCategory = 'Tabletop Games')
    ORDER BY
      SuccessScore DESC
    LIMIT
      20
  )

#SQL 7
SELECT
  scat.name AS SubCategory,
  cat.NAME AS CategoryName,
  c.outcome AS Outcome,
  ROUND(AVG(c.goal), 2) AS AvgGoal,
  ROUND(AVG(c.pledged), 2) AS AvgPledged,
  ROUND(
    (
      ROUND(AVG(c.pledged), 2) / ROUND(AVG(c.backers), 0)
    ),
    2
  ) AS AvgDonation,
  ROUND(AVG(c.backers), 0) AS AvgDonators,
  ROUND(AVG(DATEDIFF(c.deadline, c.launched)), 0) AS AvgLifeSpan,
  ROUND(AVG((c.pledged / c.goal)), 3) AS SuccessScore,
  COUNT(*) AS Entries
FROM
  campaign AS c
  JOIN currency AS cu ON c.currency_id = cu.id
  JOIN sub_category AS scat ON c.sub_category_id = scat.id
  JOIN category AS cat ON scat.category_id = cat.id
WHERE
  NOT(c.outcome = 'undefined')
  AND (c.backers > 0)
  AND (cu.NAME = 'USD')
  AND (c.outcome = 'successful')
  AND (c.launched < '2013-01-01')
GROUP BY
  c.sub_category_id,
  cat.NAME,
  c.outcome
HAVING
  COUNT(*) > 1 #At least 2 projects
  AND SuccessScore > 2
ORDER BY
  AvgPledged DESC
LIMIT
  10;
