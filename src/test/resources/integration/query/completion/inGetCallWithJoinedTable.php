<?php

\DB::query()->from('users')->join('customers', 'customer.billable_id', 'users.id')->get('<caret>');
