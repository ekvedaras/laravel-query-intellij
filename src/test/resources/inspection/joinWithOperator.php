<?php (new Illuminate\Database\Query\Builder())->from('testProject1.users')
->join('testProject1.customers', 'customers.billable_id', '=', 'users.id')
->get('users.id');
