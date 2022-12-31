<?php

\DB::query()->from('users')->join('customers as c1', 'c1.billable_id', 'users.id')->get('<caret>');
