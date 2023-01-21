<?php

use Illuminate\Database\Query\JoinClause;

\DB::query()->from('users')->join('customers', function (JoinClause $customers) {
    $customers->on('<caret>');
});
