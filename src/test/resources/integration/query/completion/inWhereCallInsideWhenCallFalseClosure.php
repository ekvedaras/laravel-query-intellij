<?php

use Illuminate\Database\Eloquent\Builder;

\DB::query()->from('users')->when(false, function (Builder $query) {

}, function (Builder $query) {
    $query->where('<caret>');
});
