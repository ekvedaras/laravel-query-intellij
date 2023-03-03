<?php

use Illuminate\Database\Eloquent\Builder;

\DB::query()->from('users')->firstWhere(function (Builder $query) {
    $query->firstWhere('<caret>');
});
