<?php

\DB::query()->from('users')->get([
    ['testProject1.users.email', '=', 'first_name'],
]);
