<?php

use Illuminate\Database\Eloquent\Builder;

\DB::query()->from('users')->when(true, function (Builder $query) {
    $query->join('customers', 'billable_id', 'email');
}, function (Builder $query) {
    $query->join('customers', 'billable_id', 'first_name');
})->get(['<caret>']);
