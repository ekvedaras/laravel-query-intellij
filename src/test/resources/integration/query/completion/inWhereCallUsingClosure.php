<?php

use Illuminate\Database\Eloquent\Builder;

\DB::query()->from('users')->where(function (Builder $query) {
    $query->where('<caret>');
});
