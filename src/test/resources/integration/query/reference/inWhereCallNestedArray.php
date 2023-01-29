<?php

\DB::query()->from('users')->where([
    ['email', '=', 'first_name'],
]);
