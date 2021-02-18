<?php (new Illuminate\Database\Query\Builder())->from('testProject1.users')
->join('testProject1.customers', function (Illuminate\Database\Query\JoinClause $jobs) {
    $jobs->on('<caret>');
})
->get('users.id');