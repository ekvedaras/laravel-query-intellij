<?php

use Illuminate\Database\Eloquent\Builder;

\DB::query()->from('users')->when(true, function (Builder $query) {
    $query->where('<caret>');
});
