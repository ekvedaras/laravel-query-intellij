<?php

DB::query()->from('users')->whereColumn([
    ['users.id', '=', '<caret>']
]);
