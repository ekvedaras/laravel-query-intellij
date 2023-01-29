<?php (new Illuminate\Database\Query\Builder())
    ->from('testProject1.users')
    ->join('testProject1.customers', 'testProject1.customers.billable_id', 'testProject1.users.email')
    ->get();
