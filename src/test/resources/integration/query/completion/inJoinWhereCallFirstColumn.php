<?php

\DB::query()->from('users')->joinWhere('customers', '<caret>');
