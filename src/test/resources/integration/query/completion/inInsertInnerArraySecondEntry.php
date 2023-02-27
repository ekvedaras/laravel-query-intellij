<?php

\DB::query()->from('users')->insert([
    ['first_name', '<caret>'],
]);
